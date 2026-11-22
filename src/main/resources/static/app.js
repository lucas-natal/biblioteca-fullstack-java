const $ = id => document.getElementById(id);
const esc = t => String(t).replace(/[&<>"']/g,
    c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const dataBR = d => d ? d.split('-').reverse().join('/') : '';

let tipoLogin = 'usuario';   // aba ativa na tela de login
let todosLivros = [];

// Sessão: { tipo: 'usuario' | 'funcionario', dados: {...} }
const getSessao = () => JSON.parse(sessionStorage.getItem('sessao'));
const getUsuario = () => {
    const s = getSessao();
    return s && s.tipo === 'usuario' ? s.dados : null;
};

// Cada área exige um tipo de login
const AREA = { livros: 'funcionario', emprestimos: 'usuario' };

// Chamada à API: devolve o JSON ou lança erro com a mensagem do servidor
async function api(url, opcoes = {}) {
    const r = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...opcoes });
    const texto = await r.text();
    if (!r.ok) throw new Error(texto || 'Erro ' + r.status);
    return texto ? JSON.parse(texto) : null;
}

function aviso(texto, erro = false) {
    $('msg').textContent = texto;
    $('msg').className = erro ? 'erro' : 'ok';
}

function mostrar(tela) {
    const exigido = AREA[tela];
    if (exigido) {
        const s = getSessao();
        if (!s) {                       // visitante: vai para a aba de login certa
            escolherAba(exigido);
            tela = 'login';
        } else if (s.tipo !== exigido) { // logado com o tipo errado
            aviso(exigido === 'funcionario'
                ? 'Área exclusiva de funcionários.' : 'Área exclusiva de usuários.', true);
            return;
        }
    }
    $('msg').textContent = '';
    ['publica', 'login', 'cadastro', 'livros', 'emprestimos']
        .forEach(id => $(id).hidden = id !== tela);
    document.querySelectorAll('nav button')
        .forEach(b => b.classList.toggle('ativo', b.dataset.tela === tela));
    if (tela === 'publica') carregarLivros();
    if (tela === 'emprestimos') carregarEmprestimos();
    if (tela === 'livros') carregarUsuarios();
    atualizarSessao();
}

// ---------- cabeçalho: menu e perfil ----------
function atualizarSessao() {
    const s = getSessao();
    // Logado, cada tipo vê só o seu menu de gerenciamento
    $('navLivros').hidden = !!s && s.tipo !== 'funcionario';
    $('navEmprestimos').hidden = !!s && s.tipo !== 'usuario';
    if (!s) { $('sessao').innerHTML = ''; return; }

    const p = s.dados;
    const func = s.tipo === 'funcionario';
    $('sessao').innerHTML = `
        <button id="btnPerfil" class="perfil">👤 Perfil</button>
        <div id="menuPerfil" class="menu-perfil" hidden>
            <span class="selo">${func ? 'Funcionário' : 'Usuário'}</span>
            <h3>${esc(p.nome)}</h3>
            <p><b>${func ? 'ID funcional' : 'Registro'}:</b> ${func ? p.id : p.registro}</p>
            <p><b>E-mail:</b> ${esc(p.email)}</p>
            <p><b>Telefone:</b> ${esc(p.telefone)}</p>
            <p><b>Nascimento:</b> ${dataBR(p.dataNascimento)}</p>
            <button id="btnSair">Sair</button>
        </div>`;
    $('btnPerfil').onclick = () => $('menuPerfil').hidden = !$('menuPerfil').hidden;
    $('btnSair').onclick = () => { sessionStorage.removeItem('sessao'); mostrar('publica'); };
}

// ---------- área pública ----------
async function carregarLivros() {
    try { todosLivros = await api('/api/livros'); desenharCards(); }
    catch (e) { aviso(e.message, true); }
}

function desenharCards() {
    const filtro = $('busca').value.toLowerCase();
    $('cards').innerHTML = todosLivros
        .filter(l => l.titulo.toLowerCase().includes(filtro))
        .map(l => {
            const livres = l.exemplares.filter(e => e.status === 'DISPONIVEL');
            return `<div class="card ${livres.length ? '' : 'indisp'}">
                <h3>${esc(l.titulo)}</h3>
                <p class="autor">${esc(l.autor)}</p>
                <p class="meta">Publicado em ${l.anoPublicacao}</p>
                <p class="meta">Código do livro: ${l.codigo}</p>
                <span class="badge ${livres.length ? 'sim' : 'nao'}">
                    ${livres.length ? livres.length + ' disponível(is)' : 'Indisponível'}</span>
                ${livres.length ? `<p class="meta">Exemplares livres: ${livres.map(e => e.codigo).join(', ')}</p>` : ''}
            </div>`;
        }).join('');
}

// ---------- login ----------
function escolherAba(tipo) {
    tipoLogin = tipo;
    $('abaUsuario').classList.toggle('ativa', tipo === 'usuario');
    $('abaFuncionario').classList.toggle('ativa', tipo === 'funcionario');
    $('registro').placeholder = tipo === 'usuario' ? 'Número de registro' : 'ID funcional';
    $('linhaCadastro').hidden = tipo !== 'usuario';   // só usuários criam conta
}
$('abaUsuario').onclick = () => escolherAba('usuario');
$('abaFuncionario').onclick = () => escolherAba('funcionario');
$('senha').onkeydown = ev => { if (ev.key === 'Enter') $('btnLogin').click(); };

$('btnLogin').onclick = async () => {
    const rota = tipoLogin === 'funcionario' ? '/api/login/funcionario' : '/api/login';
    try {
        const dados = await api(rota, { method: 'POST', body: JSON.stringify({
            registro: Number($('registro').value), senha: $('senha').value }) });
        sessionStorage.setItem('sessao', JSON.stringify({ tipo: tipoLogin, dados }));
        $('senha').value = '';
        mostrar(tipoLogin === 'funcionario' ? 'livros' : 'emprestimos');
    } catch (e) { aviso('Registro/ID ou senha inválidos.', true); }
};

$('irCadastro').onclick = ev => { ev.preventDefault(); mostrar('cadastro'); };
$('irLogin').onclick = ev => { ev.preventDefault(); escolherAba('usuario'); mostrar('login'); };

$('formCadastro').onsubmit = async ev => {
    ev.preventDefault();
    const f = ev.target;
    try {
        const u = await api('/api/usuarios', { method: 'POST', body: JSON.stringify({
            registro: +f.registro.value, nome: f.nome.value,
            dataNascimento: f.dataNascimento.value,
            telefone: f.telefone.value, senha: f.senha.value }) });
        sessionStorage.setItem('sessao', JSON.stringify({ tipo: 'usuario', dados: u }));
        mostrar('emprestimos');
        aviso('Conta criada! Bem-vindo(a), ' + u.nome);
    } catch (e) { aviso(e.message, true); }
};

// ---------- gerenciar livros (funcionário) ----------
$('formLivro').onsubmit = async ev => {
    ev.preventDefault();
    const f = ev.target;
    try {
        await api('/api/livros', { method: 'POST', body: JSON.stringify({
            codigo: +f.codigo.value, titulo: f.titulo.value,
            autor: f.autor.value, anoPublicacao: +f.anoPublicacao.value }) });
        aviso('Livro cadastrado!'); f.reset();
    } catch (e) { aviso(e.message, true); }
};

$('formExemplar').onsubmit = async ev => {
    ev.preventDefault();
    const f = ev.target;
    try {
        await api(`/api/livros/${f.codigoLivro.value}/exemplares`, {
            method: 'POST', body: JSON.stringify({ codigo: +f.codigo.value }) });
        aviso('Exemplar cadastrado!'); f.reset();
    } catch (e) { aviso(e.message, true); }
};

async function carregarUsuarios() {
    try {
        const lista = await api('/api/usuarios');
        $('listaUsuarios').innerHTML = lista.map(u => `<tr><td>${u.registro}</td>
            <td>${esc(u.nome)}</td><td>${esc(u.email)}</td><td>${esc(u.telefone)}</td></tr>`).join('')
            || '<tr><td colspan="4">Nenhum usuário cadastrado.</td></tr>';
    } catch (e) { aviso(e.message, true); }
}

// Busca um exemplar, mostra seu status e permite removê-lo (DELETE)
async function mostrarExemplar(codigo) {
    try {
        const ex = await api('/api/exemplares/' + codigo);
        const livre = ex.status === 'DISPONIVEL';
        $('resultadoExemplar').innerHTML = `<div class="resultado">
            <p><b>Exemplar ${ex.codigo}</b>
               <span class="badge ${livre ? 'sim' : 'nao'}">${livre ? 'Disponível' : 'Emprestado'}</span></p>
            <p>Livro: ${esc(ex.tituloLivro)} (código ${ex.codigoLivro})</p>
            <button id="btnRemover" class="perigo">Remover exemplar</button></div>`;
        $('btnRemover').onclick = async () => {
            if (!confirm('Remover o exemplar ' + codigo + '?')) return;
            try {
                await api('/api/exemplares/' + codigo, { method: 'DELETE' });
                $('resultadoExemplar').innerHTML = '';
                aviso('Exemplar removido!');
            } catch (e) { aviso(e.message, true); }
        };
    } catch (e) { $('resultadoExemplar').innerHTML = ''; aviso(e.message, true); }
}
$('formBuscaExemplar').onsubmit = ev => {
    ev.preventDefault();
    mostrarExemplar(+ev.target.codigo.value);
};

// ---------- empréstimos (usuário) ----------
$('formEmprestimo').onsubmit = async ev => {
    ev.preventDefault();
    const f = ev.target;
    try {
        await api('/api/emprestimos', { method: 'POST', body: JSON.stringify({
            registroUsuario: getUsuario().registro, codigoExemplar: +f.codigoExemplar.value }) });
        aviso('Empréstimo registrado!'); f.reset();
        carregarEmprestimos();
    } catch (e) { aviso(e.message, true); }
};

async function carregarEmprestimos() {
    try {
        const lista = await api('/api/emprestimos/usuario/' + getUsuario().registro);
        $('listaEmprestimos').innerHTML = lista.map(e => {
            const status = !e.ativo
                ? `<span class="badge nao">Devolvido em ${dataBR(e.dataDevolucaoReal)}</span>`
                : e.atrasado ? '<span class="badge atraso">Atrasado</span>'
                             : '<span class="badge sim">Ativo</span>';
            const botao = e.ativo ? `<button onclick="devolver(${e.id})">Devolver</button>` : '';
            return `<tr><td>${esc(e.tituloLivro)}</td><td>${e.codigoExemplar}</td>
                <td>${dataBR(e.dataEmprestimo)}</td><td>${dataBR(e.dataDevolucaoPrevista)}</td>
                <td>${status}</td><td>${botao}</td></tr>`;
        }).join('') || '<tr><td colspan="6">Nenhum empréstimo ainda.</td></tr>';
    } catch (e) { aviso(e.message, true); }
}

async function devolver(id) {
    try {
        await api(`/api/emprestimos/${id}/devolucao`, { method: 'PUT' });
        aviso('Devolução registrada!');
        carregarEmprestimos();
    } catch (e) { aviso(e.message, true); }
}

// ---------- início ----------
document.querySelectorAll('nav button').forEach(b => b.onclick = () => mostrar(b.dataset.tela));
document.addEventListener('click', ev => {          // clicar fora fecha o menu de perfil
    const menu = $('menuPerfil');
    if (menu && !$('sessao').contains(ev.target)) menu.hidden = true;
});
$('busca').oninput = desenharCards;
mostrar('publica');