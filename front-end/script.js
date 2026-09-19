let perfilAtual = null;

let ofertas = [
    {
        id: 1,
        alimento: "Arroz",
        quantidade: 40,
        unidade: "kg",
        data: "2026-09-22",
        local: "Mercado Esperança"
    },
    {
        id: 2,
        alimento: "Feijão",
        quantidade: 25,
        unidade: "kg",
        data: "2026-09-24",
        local: "Supermercado Central"
    },
    {
        id: 3,
        alimento: "Leite",
        quantidade: 30,
        unidade: "litros",
        data: "2026-09-20",
        local: "Padaria Vida"
    }
];

let solicitacoes = [
    {
        alimento: "Arroz",
        quantidade: 10,
        unidade: "kg",
        estabelecimento: "Mercado Esperança",
        status: "Solicitada",
        retirada: "2026-09-22"
    }
];

let demandas = [
    {
        alimento: "Arroz",
        quantidade: 60,
        unidade: "kg",
        beneficiarios: 30,
        prioridade: "Alta",
        prazo: "2026-09-25",
        finalidade: "Refeições semanais"
    }
];


/* =========================
   NAVEGAÇÃO
========================= */

function mostrarPagina(id) {

    document.querySelectorAll(".page").forEach(page => {
        page.classList.remove("active");
    });

    document.getElementById(id).classList.add("active");

    document.querySelectorAll(".menu").forEach(menu => {
        menu.classList.remove("active");
    });

    const menu = document.querySelector(`[data-page="${id}"]`);

    if (menu) {
        menu.classList.add("active");
    }
}


/* =========================
   PERFIL
========================= */

function selecionarPerfil(perfil) {

    perfilAtual = perfil;

    document.getElementById("modalPerfil").style.display = "none";

    const menuDoador = document.getElementById("menuDoador");

    if (perfil === "doador") {

        menuDoador.style.display = "block";

        document.getElementById("nomePerfil").textContent =
            "Mercado Esperança";

        document.getElementById("tipoPerfil").textContent =
            "Estabelecimento doador";

    } else {

        menuDoador.style.display = "none";

        document.getElementById("nomePerfil").textContent =
            "Instituição Esperança";

        document.getElementById("tipoPerfil").textContent =
            "Instituição beneficiária";
    }
}


/* =========================
   OFERTAS
========================= */

function renderizarOfertas(lista = ofertas) {

    const container = document.getElementById("listaOfertas");

    container.innerHTML = "";

    if (lista.length === 0) {

        container.innerHTML =
            "<p>Nenhuma oferta encontrada.</p>";

        return;
    }

    lista.forEach(oferta => {

        const div = document.createElement("div");

        div.className = "offer";

        div.innerHTML = `
            <h3>${oferta.alimento}</h3>

            <p>
                <strong>${oferta.quantidade}
                ${oferta.unidade}</strong> disponíveis
            </p>

            <p>
                Retirada até:
                ${formatarData(oferta.data)}
            </p>

            <p>
                Local:
                ${oferta.local}
            </p>

            ${
                perfilAtual === "instituicao"
                ?
                `<button
                    class="primary"
                    onclick="solicitarOferta(${oferta.id})">
                    Solicitar alimento
                </button>`
                :
                ""
            }
        `;

        container.appendChild(div);
    });
}


function filtrarOfertas() {

    const alimento =
        document.getElementById("filtroAlimento")
        .value
        .toLowerCase();

    const quantidade =
        Number(
            document.getElementById("filtroQuantidade").value
        ) || 0;

    const data =
        document.getElementById("filtroData").value;

    const resultado = ofertas.filter(oferta => {

        return (
            (!alimento ||
                oferta.alimento
                    .toLowerCase()
                    .includes(alimento))
            &&
            oferta.quantidade >= quantidade
            &&
            (!data || oferta.data <= data)
        );
    });

    renderizarOfertas(resultado);
}


/* =========================
   SOLICITAÇÃO
========================= */

function solicitarOferta(id) {

    if (perfilAtual !== "instituicao") {

        alert(
            "Somente instituições habilitadas podem solicitar alimentos."
        );

        return;
    }

    const oferta =
        ofertas.find(item => item.id === id);

    const quantidade = Number(
        prompt(
            `Quantidade disponível: ${oferta.quantidade} ${oferta.unidade}\n\n` +
            "Informe a quantidade desejada:"
        )
    );

    if (!quantidade || quantidade <= 0) {
        return;
    }

    if (quantidade > oferta.quantidade) {

        alert(
            "A quantidade solicitada não pode ser superior à quantidade disponível."
        );

        return;
    }

    oferta.quantidade -= quantidade;

    solicitacoes.push({

        alimento: oferta.alimento,

        quantidade: quantidade,

        unidade: oferta.unidade,

        estabelecimento: oferta.local,

        status: "Solicitada",

        retirada: oferta.data

    });

    renderizarOfertas();

    renderizarSolicitacoes();

    atualizarContadores();

    alert("Solicitação registrada com sucesso.");
}


/* =========================
   CADASTRO DE OFERTA
========================= */

document
    .getElementById("formOferta")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const novaOferta = {

            id: Date.now(),

            alimento:
                document.getElementById(
                    "ofertaAlimento"
                ).value,

            quantidade:
                Number(
                    document.getElementById(
                        "ofertaQuantidade"
                    ).value
                ),

            unidade:
                document.getElementById(
                    "ofertaUnidade"
                ).value,

            data:
                document.getElementById(
                    "ofertaData"
                ).value,

            local:
                document.getElementById(
                    "ofertaLocal"
                ).value
        };

        ofertas.push(novaOferta);

        this.reset();

        renderizarOfertas();

        atualizarContadores();

        alert("Oferta cadastrada com sucesso.");

        mostrarPagina("ofertas");
    });


/* =========================
   DEMANDA
========================= */

function calcularReferencia() {

    const beneficiarios =
        Number(
            document.getElementById(
                "beneficiarios"
            ).value
        );

    const consumo =
        Number(
            document.getElementById(
                "consumoPerCapita"
            ).value
        );

    const quantidade =
        Number(
            document.getElementById(
                "demandaQuantidade"
            ).value
        );

    if (!beneficiarios || !consumo) {

        document.getElementById(
            "referencia"
        ).hidden = true;

        document.getElementById(
            "alertaDemanda"
        ).hidden = true;

        return;
    }

    const referencia =
        beneficiarios * consumo;

    document.getElementById(
        "valorReferencia"
    ).textContent =
        referencia.toLocaleString("pt-BR");

    document.getElementById(
        "referencia"
    ).hidden = false;

    if (quantidade > referencia * 1.2) {

        document.getElementById(
            "alertaDemanda"
        ).hidden = false;

    } else {

        document.getElementById(
            "alertaDemanda"
        ).hidden = true;
    }
}


[
    "beneficiarios",
    "consumoPerCapita",
    "demandaQuantidade"
].forEach(id => {

    document
        .getElementById(id)
        .addEventListener(
            "input",
            calcularReferencia
        );
});


document
    .getElementById("formDemanda")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const beneficiarios =
            Number(
                document.getElementById(
                    "beneficiarios"
                ).value
            );

        const consumo =
            Number(
                document.getElementById(
                    "consumoPerCapita"
                ).value
            );

        const quantidade =
            Number(
                document.getElementById(
                    "demandaQuantidade"
                ).value
            );

        const referencia =
            beneficiarios * consumo;

        const excepcional =
            quantidade > referencia * 1.2;

        const justificativa =
            document.getElementById(
                "justificativa"
            ).value.trim();

        if (excepcional && !justificativa) {

            alert(
                "Informe uma justificativa para a demanda excepcional."
            );

            return;
        }

        demandas.push({

            alimento:
                document.getElementById(
                    "demandaAlimento"
                ).value,

            quantidade,

            unidade:
                document.getElementById(
                    "demandaUnidade"
                ).value,

            beneficiarios,

            prioridade:
                document.getElementById(
                    "demandaPrioridade"
                ).value,

            prazo:
                document.getElementById(
                    "demandaPrazo"
                ).value,

            finalidade:
                document.getElementById(
                    "demandaFinalidade"
                ).value
        });

        this.reset();

        document.getElementById(
            "referencia"
        ).hidden = true;

        document.getElementById(
            "alertaDemanda"
        ).hidden = true;

        renderizarDemandas();

        atualizarContadores();

        alert("Demanda cadastrada com sucesso.");

        mostrarPagina("inicio");
    });


/* =========================
   SOLICITAÇÕES
========================= */

function renderizarSolicitacoes() {

    const container =
        document.getElementById(
            "listaSolicitacoes"
        );

    container.innerHTML = "";

    solicitacoes.forEach(solicitacao => {

        const div =
            document.createElement("div");

        div.className = "request";

        div.innerHTML = `

            <h3>
                ${solicitacao.alimento}
            </h3>

            <p>
                Quantidade:
                <strong>
                    ${solicitacao.quantidade}
                    ${solicitacao.unidade}
                </strong>
            </p>

            <p>
                Estabelecimento:
                ${solicitacao.estabelecimento}
            </p>

            <p>
                Status:
                <strong>
                    ${solicitacao.status}
                </strong>
            </p>

            <p>
                Retirada:
                ${formatarData(
                    solicitacao.retirada
                )}
            </p>

            ${
                solicitacao.status === "Solicitada"
                ?
                `<button
                    class="secondary"
                    onclick="confirmarRetirada(this)">
                    Confirmar retirada
                </button>`
                :
                ""
            }

        `;

        container.appendChild(div);
    });
}


function confirmarRetirada(botao) {

    const solicitacao =
        solicitacoes.find(
            item => item.status === "Solicitada"
        );

    if (solicitacao) {

        solicitacao.status =
            "Retirada confirmada";

        renderizarSolicitacoes();

        alert(
            "Retirada confirmada com sucesso."
        );
    }
}


/* =========================
   DEMANDAS
========================= */

function renderizarDemandas() {

    const container =
        document.getElementById(
            "listaDemandas"
        );

    container.innerHTML = "";

    demandas.forEach(demanda => {

        const div =
            document.createElement("div");

        div.className = "demand";

        div.innerHTML = `

            <h3>
                ${demanda.alimento}
            </h3>

            <p>
                Quantidade:
                <strong>
                    ${demanda.quantidade}
                    ${demanda.unidade}
                </strong>
            </p>

            <p>
                Beneficiários:
                ${demanda.beneficiarios}
            </p>

            <p>
                Prioridade:
                <strong>
                    ${demanda.prioridade}
                </strong>
            </p>

            <p>
                Prazo:
                ${formatarData(
                    demanda.prazo
                )}
            </p>

            <p>
                Finalidade:
                ${demanda.finalidade}
            </p>

        `;

        container.appendChild(div);
    });
}


/* =========================
   CONTADORES
========================= */

function atualizarContadores() {

    document.getElementById(
        "totalOfertas"
    ).textContent = ofertas.length;

    document.getElementById(
        "totalSolicitacoes"
    ).textContent =
        solicitacoes.length;

    document.getElementById(
        "totalDemandas"
    ).textContent =
        demandas.length;
}


/* =========================
   DATA
========================= */

function formatarData(data) {

    if (!data) {
        return "-";
    }

    const partes = data.split("-");

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}


/* =========================
   MENU
========================= */

document.querySelectorAll(".menu")
    .forEach(menu => {

        menu.addEventListener(
            "click",
            function() {

                mostrarPagina(
                    this.dataset.page
                );

                if (
                    this.dataset.page ===
                    "ofertas"
                ) {
                    renderizarOfertas();
                }

                if (
                    this.dataset.page ===
                    "solicitacoes"
                ) {
                    renderizarSolicitacoes();
                }

                if (
                    this.dataset.page ===
                    "demandas"
                ) {
                    renderizarDemandas();
                }
            }
        );
    });


/* =========================
   SAIR
========================= */

document
    .getElementById("logoutBtn")
    .addEventListener("click", function() {

        document.getElementById(
            "modalPerfil"
        ).style.display = "flex";

    });


/* =========================
   INICIALIZAÇÃO
========================= */

document.getElementById(
    "modalPerfil"
).style.display = "flex";

renderizarOfertas();

renderizarSolicitacoes();

renderizarDemandas();

atualizarContadores();