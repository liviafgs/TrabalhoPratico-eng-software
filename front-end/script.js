// ============================================
// CONEXÃO SOLIDÁRIA
// ============================================

let perfilAtual = null;

// ============================================
// DADOS INICIAIS
// ============================================

let ofertas = [
    {
        id: 1,
        alimento: "Arroz",
        quantidade: 50,
        unidade: "kg",
        data: "2026-10-30",
        local: "Centro de distribuição",
        status: "Disponível"
    },
    {
        id: 2,
        alimento: "Feijão",
        quantidade: 30,
        unidade: "kg",
        data: "2026-10-29",
        local: "Banco de alimentos",
        status: "Disponível"
    },
    {
        id: 3,
        alimento: "Leite",
        quantidade: 20,
        unidade: "litros",
        data: "2026-10-28",
        local: "Centro comunitário",
        status: "Disponível"
    }
];

let solicitacoes = [
    {
        id: 100,
        ofertaId: 1,
        alimento: "Arroz",
        quantidade: 10,
        unidade: "kg",
        data: "2026-10-26",
        status: "Solicitada"
    }
];

let demandas = [
    {
        id: 200,
        alimento: "Feijão",
        quantidade: 25,
        unidade: "kg",
        beneficiarios: 30,
        consumoPerCapita: 0.5,
        prazo: "2026-11-05",
        finalidade: "Preparação de refeições",
        prioridade: "Alta",
        justificativa: ""
    }
];

// ============================================
// ELEMENTOS DO DOM
// ============================================

const modalPerfil = document.getElementById("modalPerfil");
const menuDoador = document.getElementById("menuDoador");

// ============================================
// NAVEGAÇÃO
// ============================================

function mostrarPagina(id) {
    const pagina = document.getElementById(id);

    if (!pagina || !pagina.classList.contains("page")) {
        console.error(`Página não encontrada: ${id}`);
        return;
    }

    const paginasDoador = ["novaOferta", "demandas"];

    if (
        paginasDoador.includes(id) &&
        perfilAtual !== "doador"
    ) {
        alert("Esta página está disponível apenas para doadores.");
        return;
    }

    document.querySelectorAll(".page").forEach((item) => {
        item.classList.remove("active");
    });

    pagina.classList.add("active");

    document.querySelectorAll(".menu[data-page]").forEach((botao) => {
        botao.classList.toggle(
            "active",
            botao.dataset.page === id
        );
    });

    if (id === "ofertas") {
        renderizarOfertas();
    }

    if (id === "solicitacoes") {
        renderizarSolicitacoes();
    }

    if (id === "demandas") {
        renderizarDemandas();
    }

    atualizarContadores();
}

// ============================================
// SELEÇÃO DE PERFIL
// ============================================

function selecionarPerfil(perfil) {
    if (perfil !== "doador" && perfil !== "instituicao") {
        alert("Perfil inválido.");
        return;
    }

    perfilAtual = perfil;

    modalPerfil.style.display = "none";

    if (perfil === "doador") {
        menuDoador.style.display = "block";

        document.getElementById("nomePerfil").textContent =
            "Estabelecimento doador";

        document.getElementById("tipoPerfil").textContent =
            "Perfil de estabelecimento doador";
    } else {
        menuDoador.style.display = "none";

        document.getElementById("nomePerfil").textContent =
            "Instituição Esperança";

        document.getElementById("tipoPerfil").textContent =
            "Instituição beneficiária";
    }

    mostrarPagina("inicio");
}

// ============================================
// ENCERRAR SESSÃO
// ============================================

document.getElementById("logoutBtn").addEventListener("click", () => {
    perfilAtual = null;

    menuDoador.style.display = "none";

    modalPerfil.style.display = "flex";

    mostrarPagina("inicio");
});

// ============================================
// FORMATAÇÃO
// ============================================

function formatarData(data) {
    if (!data) {
        return "-";
    }

    const partes = data.split("-");

    if (
        partes.length !== 3 ||
        partes.some((parte) => parte === "")
    ) {
        return data;
    }

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function escaparHTML(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, (caractere) => {
        const caracteres = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        };

        return caracteres[caractere];
    });
}

// ============================================
// CONTADORES
// ============================================

function atualizarContadores() {
    document.getElementById("totalOfertas").textContent =
        ofertas.filter((oferta) => oferta.quantidade > 0).length;

    document.getElementById("totalSolicitacoes").textContent =
        solicitacoes.length;

    document.getElementById("totalDemandas").textContent =
        demandas.length;
}

// ============================================
// OFERTAS
// ============================================

function renderizarOfertas(lista = ofertas) {
    const container = document.getElementById("listaOfertas");

    const ofertasDisponiveis = lista.filter(
        (oferta) => oferta.quantidade > 0
    );

    if (ofertasDisponiveis.length === 0) {
        container.innerHTML = `
            <p class="empty-message">
                Nenhuma oferta encontrada.
            </p>
        `;
        return;
    }

    container.innerHTML = ofertasDisponiveis.map((oferta) => `
        <article class="oferta-card">
            <h3>${escaparHTML(oferta.alimento)}</h3>

            <p>
                <strong>Quantidade:</strong>
                ${escaparHTML(oferta.quantidade)}
                ${escaparHTML(oferta.unidade)}
            </p>

            <p>
                <strong>Retirada até:</strong>
                ${formatarData(oferta.data)}
            </p>

            <p>
                <strong>Local:</strong>
                ${escaparHTML(oferta.local)}
            </p>

            ${
                perfilAtual === "instituicao"
                    ? `
                        <button
                            class="primary"
                            type="button"
                            onclick="solicitarOferta(${Number(oferta.id)})"
                        >
                            Solicitar oferta
                        </button>
                    `
                    : ""
            }
        </article>
    `).join("");
}

function filtrarOfertas() {
    const alimento = document
        .getElementById("filtroAlimento")
        .value
        .trim()
        .toLowerCase();

    const quantidadeMinima = Number(
        document.getElementById("filtroQuantidade").value
    );

    const dataLimite = document.getElementById("filtroData").value;

    const resultado = ofertas.filter((oferta) => {
        const correspondeAlimento =
            oferta.alimento.toLowerCase().includes(alimento);

        const correspondeQuantidade =
            oferta.quantidade >= quantidadeMinima;

        const correspondeData =
            !dataLimite || oferta.data <= dataLimite;

        return (
            oferta.quantidade > 0 &&
            correspondeAlimento &&
            correspondeQuantidade &&
            correspondeData
        );
    });

    renderizarOfertas(resultado);
}

// ============================================
// SOLICITAR OFERTA
// ============================================

function solicitarOferta(id) {
    if (perfilAtual !== "instituicao") {
        alert("Somente instituições podem solicitar ofertas.");
        return;
    }

    const oferta = ofertas.find(
        (item) => Number(item.id) === Number(id)
    );

    if (!oferta || oferta.quantidade <= 0) {
        alert("Esta oferta não está mais disponível.");
        renderizarOfertas();
        return;
    }

    const entrada = prompt(
        `Quantidade disponível: ${oferta.quantidade} ${oferta.unidade}.\n` +
        "Informe a quantidade desejada:"
    );

    if (entrada === null || entrada.trim() === "") {
        return;
    }

    const quantidade = Number(entrada);

    if (!Number.isFinite(quantidade) || quantidade <= 0) {
        alert("Informe uma quantidade válida, maior que zero.");
        return;
    }

    if (quantidade > oferta.quantidade) {
        alert("A quantidade solicitada é maior que a disponível.");
        return;
    }

    const novaSolicitacao = {
        id: Date.now(),
        ofertaId: oferta.id,
        alimento: oferta.alimento,
        quantidade: quantidade,
        unidade: oferta.unidade,
        data: new Date().toISOString().slice(0, 10),
        status: "Solicitada"
    };

    oferta.quantidade -= quantidade;

    solicitacoes.push(novaSolicitacao);

    renderizarOfertas();
    renderizarSolicitacoes();
    atualizarContadores();

    alert("Solicitação registrada com sucesso!");
}

// ============================================
// CADASTRAR OFERTA
// ============================================

document.getElementById("formOferta").addEventListener(
    "submit",
    (evento) => {
        evento.preventDefault();

        if (perfilAtual !== "doador") {
            alert("Somente doadores podem cadastrar ofertas.");
            return;
        }

        const alimento = document
            .getElementById("ofertaAlimento")
            .value
            .trim();

        const quantidade = Number(
            document.getElementById("ofertaQuantidade").value
        );

        const unidade = document.getElementById("ofertaUnidade").value;

        const data = document.getElementById("ofertaData").value;

        const local = document
            .getElementById("ofertaLocal")
            .value
            .trim();

        if (
            !alimento ||
            !Number.isFinite(quantidade) ||
            quantidade <= 0 ||
            !unidade ||
            !data ||
            !local
        ) {
            alert("Preencha todos os campos corretamente.");
            return;
        }

        ofertas.push({
            id: Date.now(),
            alimento,
            quantidade,
            unidade,
            data,
            local,
            status: "Disponível"
        });

        evento.target.reset();

        renderizarOfertas();
        atualizarContadores();

        alert("Oferta cadastrada com sucesso!");

        mostrarPagina("ofertas");
    }
);

// ============================================
// REFERÊNCIA DE DEMANDA
// ============================================

function calcularReferencia() {
    const beneficiarios = Number(
        document.getElementById("beneficiarios").value
    );

    const consumo = Number(
        document.getElementById("consumoPerCapita").value
    );

    const quantidade = Number(
        document.getElementById("demandaQuantidade").value
    );

    const referencia = document.getElementById("referencia");
    const alerta = document.getElementById("alertaDemanda");
    const valorReferencia = document.getElementById("valorReferencia");
    const justificativa = document.getElementById("justificativa");

    const dadosValidos =
        beneficiarios > 0 &&
        consumo > 0 &&
        Number.isFinite(beneficiarios) &&
        Number.isFinite(consumo);

    if (!dadosValidos) {
        referencia.hidden = true;
        alerta.hidden = true;
        justificativa.required = false;
        return;
    }

    const totalReferencia = beneficiarios * consumo;

    valorReferencia.textContent = totalReferencia.toLocaleString(
        "pt-BR",
        {
            maximumFractionDigits: 2
        }
    );

    referencia.hidden = false;

    const excepcional =
        quantidade > 0 &&
        quantidade > totalReferencia * 1.2;

    alerta.hidden = !excepcional;
    justificativa.required = excepcional;
}

[
    "beneficiarios",
    "consumoPerCapita",
    "demandaQuantidade"
].forEach((id) => {
    document.getElementById(id).addEventListener(
        "input",
        calcularReferencia
    );
});

// ============================================
// CADASTRAR DEMANDA
// ============================================

document.getElementById("formDemanda").addEventListener(
    "submit",
    (evento) => {
        evento.preventDefault();

        if (perfilAtual !== "instituicao") {
            alert("Somente instituições podem cadastrar demandas.");
            return;
        }

        const alimento = document
            .getElementById("demandaAlimento")
            .value
            .trim();

        const quantidade = Number(
            document.getElementById("demandaQuantidade").value
        );

        const unidade = document.getElementById("demandaUnidade").value;

        const beneficiarios = Number(
            document.getElementById("beneficiarios").value
        );

        const consumoPerCapita = Number(
            document.getElementById("consumoPerCapita").value
        );

        const prazo = document.getElementById("demandaPrazo").value;

        const finalidade = document
            .getElementById("demandaFinalidade")
            .value
            .trim();

        const prioridade = document.getElementById("demandaPrioridade").value;

        const justificativa = document
            .getElementById("justificativa")
            .value
            .trim();

        const dadosValidos =
            alimento &&
            Number.isFinite(quantidade) &&
            quantidade > 0 &&
            unidade &&
            Number.isInteger(beneficiarios) &&
            beneficiarios > 0 &&
            Number.isFinite(consumoPerCapita) &&
            consumoPerCapita > 0 &&
            prazo &&
            finalidade &&
            prioridade;

        if (!dadosValidos) {
            alert("Preencha todos os campos obrigatórios corretamente.");
            return;
        }

        const referencia = beneficiarios * consumoPerCapita;

        const excepcional = quantidade > referencia * 1.2;

        if (excepcional && !justificativa) {
            alert(
                "Informe uma justificativa para a quantidade acima da referência."
            );

            document.getElementById("justificativa").focus();
            return;
        }

        demandas.push({
            id: Date.now(),
            alimento,
            quantidade,
            unidade,
            beneficiarios,
            consumoPerCapita,
            prazo,
            finalidade,
            prioridade,
            justificativa: excepcional ? justificativa : ""
        });

        evento.target.reset();

        document.getElementById("referencia").hidden = true;
        document.getElementById("alertaDemanda").hidden = true;
        document.getElementById("justificativa").required = false;

        renderizarDemandas();
        atualizarContadores();

        alert("Demanda cadastrada com sucesso!");

        mostrarPagina("inicio");
    }
);

// ============================================
// SOLICITAÇÕES
// ============================================

function renderizarSolicitacoes() {
    const container = document.getElementById("listaSolicitacoes");

    if (solicitacoes.length === 0) {
        container.innerHTML = `
            <p class="empty-message">
                Você ainda não possui solicitações.
            </p>
        `;
        return;
    }

    container.innerHTML = solicitacoes.map((solicitacao) => `
        <article class="solicitacao-card">
            <h3>${escaparHTML(solicitacao.alimento)}</h3>

            <p>
                <strong>Quantidade:</strong>
                ${escaparHTML(solicitacao.quantidade)}
                ${escaparHTML(solicitacao.unidade)}
            </p>

            <p>
                <strong>Data:</strong>
                ${formatarData(solicitacao.data)}
            </p>

            <p>
                <strong>Status:</strong>
                ${escaparHTML(solicitacao.status)}
            </p>

            ${
                perfilAtual === "doador" &&
                solicitacao.status === "Solicitada"
                    ? `
                        <button
                            class="primary"
                            type="button"
                            onclick="confirmarRetirada(${Number(solicitacao.id)})"
                        >
                            Confirmar retirada
                        </button>
                    `
                    : ""
            }
        </article>
    `).join("");
}

// ============================================
// CONFIRMAR RETIRADA
// ============================================

function confirmarRetirada(id) {
    if (perfilAtual !== "doador") {
        alert("Somente doadores podem confirmar retiradas.");
        return;
    }

    // Busca a solicitação específica que foi selecionada.
    const solicitacao = solicitacoes.find(
        (item) => Number(item.id) === Number(id)
    );

    if (!solicitacao) {
        alert("Solicitação não encontrada.");
        return;
    }

    if (solicitacao.status !== "Solicitada") {
        alert("Esta solicitação já foi processada.");
        renderizarSolicitacoes();
        return;
    }

    solicitacao.status = "Retirada confirmada";

    renderizarSolicitacoes();
    atualizarContadores();

    alert("Retirada confirmada com sucesso!");
}

// ============================================
// DEMANDAS
// ============================================

function renderizarDemandas(lista = demandas) {
    const container = document.getElementById("listaDemandas");

    if (lista.length === 0) {
        container.innerHTML = `
            <p class="empty-message">
                Nenhuma demanda encontrada.
            </p>
        `;
        return;
    }

    container.innerHTML = lista.map((demanda) => `
        <article class="demanda-card">
            <h3>${escaparHTML(demanda.alimento)}</h3>

            <p>
                <strong>Quantidade:</strong>
                ${escaparHTML(demanda.quantidade)}
                ${escaparHTML(demanda.unidade)}
            </p>

            <p>
                <strong>Beneficiários:</strong>
                ${escaparHTML(demanda.beneficiarios)}
            </p>

            <p>
                <strong>Consumo per capita:</strong>
                ${escaparHTML(demanda.consumoPerCapita)}
            </p>

            <p>
                <strong>Prazo:</strong>
                ${formatarData(demanda.prazo)}
            </p>

            <p>
                <strong>Finalidade:</strong>
                ${escaparHTML(demanda.finalidade)}
            </p>

            <p>
                <strong>Prioridade:</strong>
                ${escaparHTML(demanda.prioridade)}
            </p>

            ${
                demanda.justificativa
                    ? `
                        <p>
                            <strong>Justificativa:</strong>
                            ${escaparHTML(demanda.justificativa)}
                        </p>
                    `
                    : ""
            }
        </article>
    `).join("");
}

// ============================================
// FILTRAR DEMANDAS
// ============================================

function filtrarDemandas() {
    const alimento = document
        .getElementById("filtroDemanda")
        .value
        .trim()
        .toLowerCase();

    const prioridade = document.getElementById("filtroPrioridade").value;

    const resultado = demandas.filter((demanda) => {
        const correspondeAlimento =
            demanda.alimento.toLowerCase().includes(alimento);

        const correspondePrioridade =
            !prioridade || demanda.prioridade === prioridade;

        return correspondeAlimento && correspondePrioridade;
    });

    renderizarDemandas(resultado);
}

document.getElementById("filtroDemanda").addEventListener(
    "input",
    filtrarDemandas
);

document.getElementById("filtroPrioridade").addEventListener(
    "change",
    filtrarDemandas
);

// ============================================
// NAVEGAÇÃO PELO MENU
// ============================================

document.querySelectorAll(".menu[data-page]").forEach((botao) => {
    botao.addEventListener("click", () => {
        mostrarPagina(botao.dataset.page);
    });
});

// ============================================
// INICIALIZAÇÃO
// ============================================

renderizarOfertas();
renderizarSolicitacoes();
renderizarDemandas();
atualizarContadores();