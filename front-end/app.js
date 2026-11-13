const API_BASE_URL = document.body.dataset.apiBase || "http://localhost:8080";
const TOKEN_KEY = "conexaoSolidariaToken";
const state = {
  token: localStorage.getItem(TOKEN_KEY),
  user: null,
  role: null,
  organization: null,
  serviceProfile: null,
  nutritionist: null,
  offers: [],
  demands: [],
  requests: [],
  notifications: [],
  coherenceTimer: null,
};

const byId = (id) => document.getElementById(id);

function showMessage(message, type = "info") {
  const element = byId("globalMessage");
  element.textContent = message;
  element.dataset.type = type;
  element.hidden = false;
  window.clearTimeout(showMessage.timeout);
  showMessage.timeout = window.setTimeout(() => {
    element.hidden = true;
  }, 6000);
}

function setFormBusy(form, busy, label = "Enviando...") {
  const button = form.querySelector("button[type='submit']");
  if (!button) return;
  if (busy) {
    button.dataset.originalLabel = button.textContent.trim();
    button.textContent = label;
  } else if (button.dataset.originalLabel) {
    button.textContent = button.dataset.originalLabel;
    delete button.dataset.originalLabel;
  }
  button.disabled = busy;
}

async function api(path, options = {}) {
  const headers = { Accept: "application/json" };
  if (options.body !== undefined) headers["Content-Type"] = "application/json";
  if (options.auth !== false && state.token)
    headers.Authorization = `Bearer ${state.token}`;

  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      method: options.method || "GET",
      headers,
      body:
        options.body === undefined ? undefined : JSON.stringify(options.body),
    });
  } catch (error) {
    throw new Error(
      "Não foi possível acessar a API. Confira se o backend está ativo.",
    );
  }

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = { erro: text };
    }
  }

  if (!response.ok) {
    const error = new Error(
      data?.erro || `A API respondeu com erro ${response.status}.`,
    );
    error.status = response.status;
    if (response.status === 401 && options.auth !== false) signOut(false);
    throw error;
  }
  return data;
}

function escapeHtml(value) {
  return String(value ?? "").replace(
    /[&<>"']/g,
    (character) =>
      ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;",
      })[character],
  );
}

function formatDate(value) {
  if (!value) return "-";
  const [year, month, day] = String(value).slice(0, 10).split("-");
  return year && month && day ? `${day}/${month}/${year}` : value;
}

function roleName(profile) {
  return profile === "ESTABELECIMENTO_DOADOR"
    ? "Estabelecimento doador"
    : "Instituição beneficiária";
}

function showAuth(view = "login") {
  byId("modalPerfil").style.display = "flex";
  byId("appShell").hidden = true;
  byId("cadastroOrganizacao").style.display = "none";
  byId("loginPane").hidden = view !== "login";
  byId("registerPane").hidden = view !== "register";
  byId("authFeedback").textContent = "";
}

function signOut(showNotice = true) {
  localStorage.removeItem(TOKEN_KEY);
  state.token = null;
  state.user = null;
  state.role = null;
  state.organization = null;
  state.serviceProfile = null;
  state.nutritionist = null;
  state.offers = [];
  state.demands = [];
  state.requests = [];
  state.notifications = [];
  byId("menuDoador").hidden = true;
  byId("menuInstituicao").hidden = true;
  byId("logoutBtn").hidden = true;
  showAuth();
  if (showNotice) showMessage("Sessão encerrada.");
}

async function startSession(token) {
  state.token = token;
  localStorage.setItem(TOKEN_KEY, token);
  const response = await api("/funcionalidades");
  state.user = response.usuario;
  state.role = state.user.perfil;

  if (
    !["ESTABELECIMENTO_DOADOR", "INSTITUICAO_BENEFICIARIA"].includes(state.role)
  ) {
    signOut(false);
    throw new Error("Este perfil ainda não está disponível nesta interface.");
  }

  byId("modalPerfil").style.display = "none";
  byId("logoutBtn").hidden = false;
  byId("menuDoador").hidden = state.role !== "ESTABELECIMENTO_DOADOR";
  byId("menuInstituicao").hidden = state.role !== "INSTITUICAO_BENEFICIARIA";
  byId("homeDemandLink").hidden = state.role !== "INSTITUICAO_BENEFICIARIA";
  byId("nomeUsuario").textContent = state.user.nome;
  byId("perfilUsuario").textContent = roleName(state.role);

  try {
    const path =
      state.role === "ESTABELECIMENTO_DOADOR"
        ? "/estabelecimentos/me"
        : "/instituicoes/me";
    const key =
      state.role === "ESTABELECIMENTO_DOADOR"
        ? "estabelecimento"
        : "instituicao";
    state.organization = (await api(path))[key];
    if (state.role === "INSTITUICAO_BENEFICIARIA")
      await loadInstitutionDetails();
    showApplication();
    renderProfile();
    await refreshData();
  } catch (error) {
    if (error.status !== 404) throw error;
    byId("modalPerfil").style.display = "none";
    byId("cadastroOrganizacao").style.display = "flex";
    byId("tituloCadastroOrganizacao").textContent =
      state.role === "ESTABELECIMENTO_DOADOR"
        ? "Cadastre seu estabelecimento"
        : "Cadastre sua instituição";
  }
}

function showApplication() {
  byId("modalPerfil").style.display = "none";
  byId("cadastroOrganizacao").style.display = "none";
  byId("appShell").hidden = false;
}

async function loadOffers() {
  const response = await api("/ofertas?disponivel=true");
  state.offers = response.ofertas || [];
  renderOffers();
}

async function loadDemands() {
  const response = await api("/demandas?disponivel=true");
  state.demands = response.demandas || [];
  renderDemands();
}

async function loadRequests() {
  const response = await api("/ofertas/solicitacoes");
  state.requests = response.solicitacoes || [];
  renderRequests();
}

async function loadNotifications() {
  const response = await api("/notificacoes");
  state.notifications = response.notificacoes || [];
  renderNotifications(response.naoLidas || 0);
}

async function loadInstitutionDetails() {
  const [profile, nutritionist] = await Promise.allSettled([
    api("/instituicoes/perfil-atendimento/me"),
    api("/nutricionistas/me"),
  ]);
  state.serviceProfile =
    profile.status === "fulfilled" ? profile.value.perfilAtendimento : null;
  state.nutritionist =
    nutritionist.status === "fulfilled"
      ? nutritionist.value.nutricionista
      : null;
}

async function refreshData() {
  const results = await Promise.allSettled([
    loadOffers(),
    loadDemands(),
    loadRequests(),
    loadNotifications(),
  ]);
  updateCounters();
  const failure = results.find((result) => result.status === "rejected");
  if (failure) showMessage(failure.reason.message, "error");
}

function updateCounters() {
  byId("totalOfertas").textContent = state.offers.length;
  byId("totalSolicitacoes").textContent = state.requests.length;
  byId("totalDemandas").textContent = state.demands.length;
}

function renderOffers() {
  const container = byId("listaOfertas");
  const foodFilter = byId("filtroAlimento")
    .value.trim()
    .toLocaleLowerCase("pt-BR");
  const quantityFilter = Number(byId("filtroQuantidade").value || 0);
  const dateFilter = byId("filtroData").value;
  const offers = state.offers.filter((offer) => {
    const quantity = Number(offer.quantidadeDisponivel ?? offer.quantidade);
    const date = offer.retiradaAte || offer.validade;
    return (
      offer.alimento.toLocaleLowerCase("pt-BR").includes(foodFilter) &&
      quantity >= quantityFilter &&
      (!dateFilter || !date || date <= dateFilter)
    );
  });

  if (!offers.length) {
    container.innerHTML =
      '<p class="empty-message">Nenhuma oferta encontrada.</p>';
    return;
  }

  const canRequest = state.role === "INSTITUICAO_BENEFICIARIA";
  const enabled = state.organization?.status === "HABILITADA";
  container.innerHTML = offers
    .map(
      (offer) => `
        <article class="oferta-card">
            <h3>${escapeHtml(offer.alimento)}</h3>
            <p><strong>Disponível:</strong> ${escapeHtml(offer.quantidadeDisponivel ?? offer.quantidade)} ${escapeHtml(offer.unidadeMedida)}</p>
            <p><strong>Retirada até:</strong> ${formatDate(offer.retiradaAte)}</p>
            <p><strong>Local:</strong> ${escapeHtml(offer.localRetirada || "A combinar")}</p>
            ${
              canRequest
                ? enabled
                  ? `<form class="offer-request-form" data-offer-id="${Number(offer.id)}">
                    <label for="request-quantity-${Number(offer.id)}">Quantidade desejada</label>
                    <input id="request-quantity-${Number(offer.id)}" name="quantidade" type="number" min="0.01" max="${Number(offer.quantidadeDisponivel ?? offer.quantidade)}" step="0.01" required>
                    <button class="primary" type="submit">Solicitar oferta</button>
                </form>`
                  : '<p class="hint">Habilite sua instituição em Meu cadastro para solicitar.</p>'
                : ""
            }
        </article>
    `,
    )
    .join("");
}

function renderDemands() {
  const container = byId("listaDemandas");
  const foodFilter = byId("filtroDemanda")
    .value.trim()
    .toLocaleLowerCase("pt-BR");
  const priorityFilter = byId("filtroPrioridade").value;
  const demands = state.demands.filter(
    (demand) =>
      demand.alimento.toLocaleLowerCase("pt-BR").includes(foodFilter) &&
      (!priorityFilter || demand.prioridade === priorityFilter),
  );

  if (!demands.length) {
    container.innerHTML =
      '<p class="empty-message">Nenhuma demanda encontrada.</p>';
    return;
  }
  container.innerHTML = demands
    .map(
      (demand) => `
        <article class="demanda-card">
            <h3>${escapeHtml(demand.alimento)}</h3>
            <p><strong>Quantidade:</strong> ${escapeHtml(demand.quantidade)} ${escapeHtml(demand.unidadeMedida)}</p>
            <p><strong>Instituição:</strong> ${escapeHtml(demand.instituicaoNome || "Instituição beneficiária")}</p>
            <p><strong>Beneficiários:</strong> ${escapeHtml(demand.beneficiarios)}</p>
            <p><strong>Prazo:</strong> ${formatDate(demand.prazo)}</p>
            <p><strong>Finalidade:</strong> ${escapeHtml(demand.finalidade)}</p>
            <p><strong>Prioridade:</strong> ${escapeHtml(demand.prioridade)}</p>
            ${demand.justificativa ? `<p><strong>Justificativa:</strong> ${escapeHtml(demand.justificativa)}</p>` : ""}
        </article>
    `,
    )
    .join("");
}

const statusNames = {
  SOLICITADA: "Solicitada",
  ACEITA: "Aceita",
  RECUSADA: "Recusada",
  AGENDADA: "Agendada",
  RETIRADA: "Retirada",
  CONCLUIDA: "Concluída",
  CANCELADA: "Cancelada",
};

function renderRequests() {
  const container = byId("listaSolicitacoes");
  if (!state.requests.length) {
    container.innerHTML =
      '<p class="empty-message">Você ainda não possui solicitações.</p>';
    return;
  }
  container.innerHTML = state.requests
    .map((request) => {
      const donor = state.role === "ESTABELECIMENTO_DOADOR";
      let actions = "";
      if (donor && request.status === "SOLICITADA") {
        actions = `<button class="primary" data-action="accept-request" data-id="${request.id}">Aceitar</button>
                <button class="secondary" data-action="refuse-request" data-id="${request.id}">Recusar</button>`;
      } else if (donor && request.status === "ACEITA") {
        actions = `<label for="schedule-${request.id}">Agendar retirada</label>
                <input id="schedule-${request.id}" type="datetime-local">
                <button class="primary" data-action="schedule-request" data-id="${request.id}">Salvar agendamento</button>`;
      } else if (donor && request.status === "AGENDADA") {
        actions = `<button class="primary" data-action="confirm-pickup" data-id="${request.id}">Confirmar retirada</button>`;
      } else if (!donor && request.status === "SOLICITADA") {
        actions = `<button class="secondary" data-action="cancel-request" data-id="${request.id}">Cancelar solicitação</button>`;
      } else if (!donor && request.status === "RETIRADA") {
        actions = `<button class="primary" data-action="complete-donation" data-id="${request.id}">Concluir doação</button>`;
      }
      return `<article class="solicitacao-card">
            <h3>${escapeHtml(request.alimento || `Solicitação #${request.id}`)}</h3>
            <p><strong>Quantidade:</strong> ${escapeHtml(request.quantidade)} ${escapeHtml(request.unidadeMedida)}</p>
            <p><strong>Instituição:</strong> ${escapeHtml(request.instituicaoNome || "- ")}</p>
            <p><strong>Estabelecimento:</strong> ${escapeHtml(request.estabelecimentoNome || "- ")}</p>
            <p><strong>Status:</strong> ${statusNames[request.status] || escapeHtml(request.status)}</p>
            <p><strong>Agendada para:</strong> ${request.retiradaAgendadaPara ? escapeHtml(request.retiradaAgendadaPara.replace("T", " ")) : "-"}</p>
            <div class="actions">${actions}</div>
        </article>`;
    })
    .join("");
}

function renderNotifications(unreadCount) {
  byId("totalNotificacoes").textContent = unreadCount;
  const container = byId("listaNotificacoes");
  if (!state.notifications.length) {
    container.innerHTML = '<p class="empty-message">Nenhuma notificação.</p>';
    return;
  }
  container.innerHTML = state.notifications
    .map(
      (notification) => `
        <article class="item">
            <h3>${escapeHtml(notification.tipo)}</h3>
            <p>${escapeHtml(notification.mensagem)}</p>
            <p><strong>${notification.lida ? "Lida" : "Não lida"}</strong> · ${formatDate(notification.criadaEm)}</p>
            ${notification.lida ? "" : `<button class="secondary" data-action="read-notification" data-id="${notification.id}">Marcar como lida</button>`}
        </article>
    `,
    )
    .join("");
}

function renderProfile() {
  if (!state.organization) return;
  const institution = state.role === "INSTITUICAO_BENEFICIARIA";
  byId("nomePerfil").textContent = state.organization.nome;
  byId("tipoPerfil").textContent = roleName(state.role);
  byId("statusPerfil").textContent = institution
    ? state.organization.statusDescricao ||
      statusNames[state.organization.status] ||
      state.organization.status
    : "Cadastro ativo";
  byId("statusPerfil").classList.toggle(
    "pending",
    institution && state.organization.status !== "HABILITADA",
  );
  byId("habilitarInstituicaoBtn").hidden =
    !institution || state.organization.status === "HABILITADA";
  byId("campoOrganizacaoNome").value = state.organization.nome || "";
  byId("campoOrganizacaoCnpj").value = state.organization.cnpj || "";
  byId("campoOrganizacaoEndereco").value = state.organization.endereco || "";
  byId("campoOrganizacaoContato").value = state.organization.contato || "";
  byId("formPerfilOrganizacao").hidden = false;
  byId("nomeConta").textContent = state.user.nome;
  byId("emailConta").textContent = state.user.email;
  byId("perfilAtendimento").hidden = !institution || !state.serviceProfile;
  byId("formPerfilAtendimento").hidden =
    !institution || Boolean(state.serviceProfile);
  byId("dadosNutricionista").hidden = !institution || !state.nutritionist;
  byId("formNutricionista").hidden =
    !institution || Boolean(state.nutritionist);
  if (state.serviceProfile) {
    byId("numeroBeneficiariosPerfil").textContent =
      state.serviceProfile.numeroBeneficiarios;
    byId("capacidadePerfil").textContent =
      state.serviceProfile.capacidadeAtendimento;
    byId("publicoPerfil").textContent = state.serviceProfile.publicoAtendido;
    byId("frequenciaPerfil").textContent =
      state.serviceProfile.frequenciaAtendimento;
  }
  if (state.nutritionist) {
    byId("nutricionistaNome").textContent = state.nutritionist.nome;
    byId("nutricionistaCrn").textContent = state.nutritionist.crn;
    byId("nutricionistaContato").textContent = state.nutritionist.contato;
  }
}

async function showPage(id) {
  const page = byId(id);
  if (!page?.classList.contains("page")) return;
  if (
    ["novaOferta", "demandas"].includes(id) &&
    state.role !== "ESTABELECIMENTO_DOADOR"
  )
    return;
  if (id === "demanda" && state.role !== "INSTITUICAO_BENEFICIARIA") return;
  document
    .querySelectorAll(".page")
    .forEach((item) => item.classList.toggle("active", item === page));
  document.querySelectorAll(".menu[data-page]").forEach((button) => {
    button.classList.toggle("active", button.dataset.page === id);
  });
  try {
    if (id === "ofertas") await loadOffers();
    if (id === "demandas") await loadDemands();
    if (id === "solicitacoes") await loadRequests();
    if (id === "notificacoes") await loadNotifications();
    if (id === "cadastro") renderProfile();
  } catch (error) {
    showMessage(error.message, "error");
  }
}

async function submitLogin(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Entrando...");
  try {
    const response = await api("/auth/login", {
      method: "POST",
      auth: false,
      body: {
        email: byId("loginEmail").value.trim(),
        senha: byId("loginSenha").value,
      },
    });
    await startSession(response.token);
    showMessage(`Bem-vindo(a), ${state.user.nome}.`);
  } catch (error) {
    byId("authFeedback").textContent = error.message;
  } finally {
    setFormBusy(form, false);
  }
}

async function submitAccountRegistration(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Criando conta...");
  try {
    const body = {
      nome: byId("novoNome").value.trim(),
      email: byId("novoEmail").value.trim(),
      senha: byId("novaSenha").value,
      perfil: byId("novoPerfil").value,
    };
    await api("/usuarios", { method: "POST", auth: false, body });
    const login = await api("/auth/login", {
      method: "POST",
      auth: false,
      body: { email: body.email, senha: body.senha },
    });
    await startSession(login.token);
  } catch (error) {
    byId("authFeedback").textContent = error.message;
  } finally {
    setFormBusy(form, false);
  }
}

async function submitOrganization(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Salvando cadastro...");
  try {
    const isDonor = state.role === "ESTABELECIMENTO_DOADOR";
    const key = isDonor ? "estabelecimento" : "instituicao";
    const path = isDonor ? "/estabelecimentos" : "/instituicoes";
    const response = await api(path, {
      method: "POST",
      body: {
        nome: byId("organizacaoNome").value.trim(),
        cnpj: byId("organizacaoCnpj").value.replace(/\D/g, ""),
        endereco: byId("organizacaoEndereco").value.trim(),
        contato: byId("organizacaoContato").value.trim(),
      },
    });
    state.organization = response[key];
    showApplication();
    renderProfile();
    await refreshData();
    showMessage("Cadastro da organização salvo no banco de dados.");
  } catch (error) {
    byId("organizationFeedback").textContent = error.message;
  } finally {
    setFormBusy(form, false);
  }
}

async function submitOrganizationUpdate(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Salvando...");
  try {
    const isDonor = state.role === "ESTABELECIMENTO_DOADOR";
    const key = isDonor ? "estabelecimento" : "instituicao";
    const response = await api(
      isDonor ? "/estabelecimentos" : "/instituicoes",
      {
        method: "PUT",
        body: {
          nome: byId("campoOrganizacaoNome").value.trim(),
          cnpj: byId("campoOrganizacaoCnpj").value.replace(/\D/g, ""),
          endereco: byId("campoOrganizacaoEndereco").value.trim(),
          contato: byId("campoOrganizacaoContato").value.trim(),
        },
      },
    );
    state.organization = response[key];
    renderProfile();
    showMessage("Cadastro atualizado.");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function submitServiceProfile(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Salvando...");
  try {
    const response = await api("/instituicoes/perfil-atendimento", {
      method: "POST",
      body: {
        idInstituicao: state.organization.id,
        numeroBeneficiarios: Number(byId("perfilNumeroBeneficiarios").value),
        capacidadeAtendimento: Number(byId("perfilCapacidade").value),
        publicoAtendido: byId("perfilPublico").value.trim(),
        frequenciaAtendimento: byId("perfilFrequencia").value.trim(),
      },
    });
    state.serviceProfile = response.perfilAtendimento;
    renderProfile();
    showMessage("Perfil de atendimento salvo.");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function submitNutritionist(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Salvando...");
  try {
    const response = await api("/nutricionistas", {
      method: "POST",
      body: {
        idInstituicao: state.organization.id,
        nome: byId("nutricionistaNomeInput").value.trim(),
        crn: byId("nutricionistaCrnInput").value.trim(),
        contato: byId("nutricionistaContatoInput").value.trim(),
      },
    });
    state.nutritionist = response.nutricionista;
    renderProfile();
    showMessage("Nutricionista responsável cadastrado.");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function submitOffer(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Publicando...");
  try {
    const alimento = byId("ofertaAlimento").value.trim();
    const unidadeMedida = byId("ofertaUnidade").value;
    const foodResponse = await api("/alimentos");
    let food = (foodResponse.alimentos || []).find(
      (item) =>
        item.nome.toLocaleLowerCase("pt-BR") ===
        alimento.toLocaleLowerCase("pt-BR"),
    );
    if (!food) {
      const created = await api("/alimentos", {
        method: "POST",
        body: {
          nome: alimento,
          categoria: byId("ofertaCategoria").value,
          unidadeMedida,
        },
      });
      food = created.alimento;
    }
    await api("/ofertas", {
      method: "POST",
      body: {
        idAlimento: food.id,
        alimento,
        categoria: food.categoria,
        quantidade: Number(byId("ofertaQuantidade").value),
        unidadeMedida,
        retiradaAte: byId("ofertaData").value,
        validade: byId("ofertaValidade").value || null,
        localRetirada: byId("ofertaLocal").value.trim(),
      },
    });
    form.reset();
    await Promise.all([loadOffers(), loadRequests()]);
    updateCounters();
    showMessage("Oferta publicada no banco de dados.");
    await showPage("ofertas");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

function updateDemandCoherence() {
  window.clearTimeout(state.coherenceTimer);
  const beneficiaries = Number(byId("beneficiarios").value);
  const consumption = Number(byId("consumoPerCapita").value);
  const quantity = Number(byId("demandaQuantidade").value);
  if (!(beneficiaries > 0 && consumption > 0 && quantity > 0)) {
    byId("referencia").hidden = true;
    byId("alertaDemanda").hidden = true;
    byId("justificativa").required = false;
    return;
  }

  state.coherenceTimer = window.setTimeout(async () => {
    try {
      const result = await api("/demandas/verificar-coerencia", {
        method: "POST",
        body: {
          alimento: byId("demandaAlimento").value.trim(),
          quantidade,
          unidadeMedida: byId("demandaUnidade").value,
          beneficiarios,
          consumoPerCapita: consumption,
        },
      });
      byId("referencia").hidden = false;
      byId("valorReferencia").textContent = Number(
        result.quantidadeReferencia || 0,
      ).toLocaleString("pt-BR");
      byId("alertaDemanda").hidden = !result.exigeJustificativa;
      byId("justificativa").required = Boolean(result.exigeJustificativa);
    } catch {
      const reference = beneficiaries * consumption;
      byId("referencia").hidden = false;
      byId("valorReferencia").textContent = reference.toLocaleString("pt-BR");
      const exceptional = quantity > reference * 1.2;
      byId("alertaDemanda").hidden = !exceptional;
      byId("justificativa").required = exceptional;
    }
  }, 350);
}

async function submitDemand(event) {
  event.preventDefault();
  const form = event.currentTarget;
  setFormBusy(form, true, "Registrando...");
  try {
    await api("/demandas", {
      method: "POST",
      body: {
        alimento: byId("demandaAlimento").value.trim(),
        quantidade: Number(byId("demandaQuantidade").value),
        unidadeMedida: byId("demandaUnidade").value,
        prazo: byId("demandaPrazo").value,
        prioridade: byId("demandaPrioridade").value,
        finalidade: byId("demandaFinalidade").value.trim(),
        beneficiarios: Number(byId("beneficiarios").value),
        consumoPerCapita: Number(byId("consumoPerCapita").value),
        justificativa: byId("justificativa").value.trim(),
      },
    });
    form.reset();
    byId("referencia").hidden = true;
    byId("alertaDemanda").hidden = true;
    byId("justificativa").required = false;
    await loadDemands();
    updateCounters();
    showMessage("Demanda registrada no banco de dados.");
    await showPage("inicio");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function performRequestAction(action, id, extra = {}) {
  const paths = {
    "accept-request": "/ofertas/solicitacoes/aceitar",
    "refuse-request": "/ofertas/solicitacoes/recusar",
    "schedule-request": "/ofertas/solicitacoes/agendar",
    "cancel-request": "/ofertas/solicitacoes/cancelar",
    "confirm-pickup": "/ofertas/retiradas/confirmar",
    "complete-donation": "/ofertas/solicitacoes/concluir",
  };
  const body = { idSolicitacao: id, ...extra };
  await api(paths[action], { method: "POST", body });
  await Promise.all([loadRequests(), loadOffers(), loadNotifications()]);
  updateCounters();
  showMessage("Solicitação atualizada.");
}

async function handleAction(button) {
  const { action, id } = button.dataset;
  if (action === "refresh") return refreshData();
  if (action === "schedule-request") {
    const dateTime = byId(`schedule-${id}`).value;
    if (!dateTime)
      return showMessage("Escolha data e hora para a retirada.", "error");
    return performRequestAction(action, Number(id), {
      dataHoraRetirada: dateTime,
    });
  }
  if (action === "read-notification") {
    await api("/notificacoes/ler", {
      method: "POST",
      body: { idNotificacao: Number(id) },
    });
    return loadNotifications();
  }
  if (action === "enable-institution") {
    const response = await api("/instituicoes/habilitar", {
      method: "POST",
      body: { habilitada: true },
    });
    state.organization = response.instituicao;
    renderProfile();
    renderOffers();
    return showMessage("Instituição habilitada para solicitar ofertas.");
  }
  if (action === "mark-all-read") {
    await api("/notificacoes/ler-todas", { method: "POST" });
    return loadNotifications();
  }
  if (
    action in
    {
      "accept-request": 1,
      "refuse-request": 1,
      "cancel-request": 1,
      "confirm-pickup": 1,
      "complete-donation": 1,
    }
  )
    return performRequestAction(action, Number(id));
}

byId("formLogin").addEventListener("submit", submitLogin);
byId("formConta").addEventListener("submit", submitAccountRegistration);
byId("formCadastroOrganizacao").addEventListener("submit", submitOrganization);
byId("formPerfilOrganizacao").addEventListener(
  "submit",
  submitOrganizationUpdate,
);
byId("formPerfilAtendimento").addEventListener("submit", submitServiceProfile);
byId("formNutricionista").addEventListener("submit", submitNutritionist);
byId("formOferta").addEventListener("submit", submitOffer);
byId("formDemanda").addEventListener("submit", submitDemand);

document.querySelectorAll("[data-auth-view]").forEach((button) => {
  button.addEventListener("click", () => showAuth(button.dataset.authView));
});

document.querySelectorAll(".menu[data-page]").forEach((button) => {
  button.addEventListener("click", () => showPage(button.dataset.page));
});

document.querySelectorAll("[data-page-link]").forEach((button) => {
  button.addEventListener("click", () => showPage(button.dataset.pageLink));
});

document.addEventListener("click", async (event) => {
  const button = event.target.closest("[data-action]");
  if (!button) return;
  button.disabled = true;
  try {
    await handleAction(button);
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    button.disabled = false;
  }
});

document.addEventListener("submit", async (event) => {
  const form = event.target;
  if (!form.matches(".offer-request-form")) return;
  event.preventDefault();
  const button = form.querySelector("button[type='submit']");
  const quantidade = Number(form.elements.quantidade.value);
  const idOferta = Number(form.dataset.offerId);
  if (!Number.isFinite(quantidade) || quantidade <= 0) {
    showMessage("Informe uma quantidade maior que zero.", "error");
    return;
  }
  setFormBusy(form, true, "Enviando...");
  try {
    await api("/ofertas/solicitacoes", {
      method: "POST",
      body: { idOferta, quantidade },
    });
    await Promise.all([loadOffers(), loadRequests(), loadNotifications()]);
    updateCounters();
    showMessage("Solicitação enviada.");
  } catch (error) {
    showMessage(error.message, "error");
  } finally {
    setFormBusy(form, false);
    button.disabled = false;
  }
});

byId("logoutBtn").addEventListener("click", () => signOut());
byId("filtrarOfertasBtn").addEventListener("click", renderOffers);
byId("filtroAlimento").addEventListener("input", renderOffers);
byId("filtroQuantidade").addEventListener("input", renderOffers);
byId("filtroData").addEventListener("change", renderOffers);
byId("filtroDemanda").addEventListener("input", renderDemands);
byId("filtroPrioridade").addEventListener("change", renderDemands);
byId("formCadastroOrganizacao").addEventListener("input", (event) => {
  if (event.target.id === "organizacaoCnpj")
    event.target.value = event.target.value.replace(/\D/g, "").slice(0, 14);
});
byId("formPerfilOrganizacao").addEventListener("input", (event) => {
  if (event.target.id === "campoOrganizacaoCnpj")
    event.target.value = event.target.value.replace(/\D/g, "").slice(0, 14);
});
document
  .querySelectorAll("#formDemanda input, #formDemanda select")
  .forEach((input) => {
    input.addEventListener("input", updateDemandCoherence);
  });
byId("showRegisterBtn").addEventListener("click", () => showAuth("register"));
byId("showLoginBtn").addEventListener("click", () => showAuth("login"));

showAuth("login");
if (state.token) {
  startSession(state.token).catch((error) => {
    if (error.status === 401) return;
    showAuth("login");
    byId("authFeedback").textContent = error.message;
  });
}
