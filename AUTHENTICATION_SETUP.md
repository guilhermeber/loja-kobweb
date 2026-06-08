# 🛒 Sistema de Autenticação e Admin - Loja Kobweb

## ✅ O que foi implementado

### 1. **Sistema de Autenticação**
- ✅ Login e Registro de usuários
- ✅ Validação de email e senha
- ✅ Persistência em localStorage
- ✅ 2 Roles: **CUSTOMER** e **ADMIN**

### 2. **Conta Admin Padrão**
```
Email: admin@utfpr.com
Senha: admin123
Role: ADMIN
```

### 3. **Funcionalidades de Admin**
- ✅ **Adicionar novos produtos** com nome, categoria, preço e estoque
- ✅ **Editar produtos existentes** 
- ✅ **Deletar produtos**
- ✅ **Ver total de estoque**

### 4. **Interface de Login**
- Tela de login com alternância para registro
- Validações de email e senha
- Feedback de erros/sucesso
- Link para demo da conta admin

### 5. **Painel de Admin**
- Modal com gerenciamento de produtos
- Listagem completa de produtos
- Botões de editar/deletar
- Formulário para adicionar/editar produtos com validações

---

## 🚀 Como Usar

### Primeira vez usando a aplicação:

1. **Abrir a aplicação**
   - A tela de login aparecerá

2. **Login como Admin (para testar gerenciamento)**
   ```
   Email: admin@utfpr.com
   Senha: admin123
   ```

3. **Ou criar uma conta de cliente**
   - Clique em "Registre-se"
   - Preencha email (ex: cliente@email.com)
   - Preencha senha (mínimo 6 caracteres)

### Como Admin:

1. **Acessar Painel de Admin**
   - Após login, clique no botão "🔧 Admin" na barra superior

2. **Adicionar Produto**
   - Clique em "+ Adicionar Produto"
   - Preencha: nome, categoria, preço, estoque
   - Clique em "Adicionar"

3. **Editar Produto**
   - Na lista de produtos, clique em "Editar"
   - Modifique os campos desejados
   - Clique em "Atualizar"

4. **Deletar Produto**
   - Na lista de produtos, clique em "Deletar"
   - O produto será removido

### Como Cliente:

1. **Navegar pela loja**
   - Browse produtos com filtros
   - Buscar por nome
   - Filtrar por categoria e preço
   - Filtrar por estoque

2. **Comprar**
   - Adicionar produtos ao carrinho
   - Abrir carrinho
   - Escolher método de pagamento (PIX, Boleto, Cartão de Crédito)
   - Finalizar compra

3. **Ver histórico**
   - Visualizar compras recentes
   - Clique em "Compras Recentes" na barra de navegação

---

## 📁 Arquivos Criados

```
src/jsMain/kotlin/br/edu/utfpr/loja_kobweb/
├── model/
│   ├── User.kt           ← Modelo de usuário
│   └── UserRole.kt       ← Enum de roles (CUSTOMER, ADMIN)
├── store/
│   └── AuthStore.kt      ← Gerenciador de autenticação
├── ui/
│   ├── LoginScreen.kt    ← Tela de login/registro
│   └── AdminPanel.kt     ← Painel de administração
└── (arquivos existentes modificados)
    └── App.kt            ← Integração de autenticação
    └── Store.kt          ← Métodos de admin adicionados
```

---

## 🔐 Dados Persistidos

Tudo é salvo automaticamente em **localStorage**:
- Usuários cadastrados
- Usuário logado atualmente
- Produtos cadastrados
- Carrinho de compras
- Histórico de compras

### Limpar dados (Desenvolvedor)
Para resetar tudo, abra o console do navegador e execute:
```javascript
localStorage.clear()
// Recarregue a página
```

---

## 🎨 Fluxo de Autenticação

```
Usuário abre app
    ↓
Verifica se está logado?
    ├─ NÃO → Mostra LoginScreen
    │        ├─ Fazer Login → Store (se existir)
    │        └─ Registrar → Cria novo usuário e loga
    │
    └─ SIM → Mostra StoreScreen
             ├─ Se ADMIN → Mostra botão "🔧 Admin"
             └─ Se CUSTOMER → Mostra apenas loja
```

---

## 🔄 Fluxo de Gerenciamento de Produtos (Admin)

```
Admin clica "🔧 Admin"
    ↓
Abre AdminPanel
    ├─ Ver produtos (+botões Editar/Deletar)
    ├─ Clica "+ Adicionar Produto" → AddEditProductDialog
    │  ├─ Preenche campos
    │  └─ Salva em Store
    │
    ├─ Clica "Editar" → AddEditProductDialog (pre-preenchido)
    │  ├─ Modifica campos
    │  └─ Atualiza em Store
    │
    └─ Clica "Deletar"
       └─ Remove produto
```

---

## ⚠️ Observações de Segurança

**⚠️ AVISO IMPORTANTE:**
Este é um sistema de **demonstração/aprendizado**. Para produção:

1. **Não armazene senhas em plain text**
   - Use bcrypt ou PBKDF2 para hash
   - Use JWT para tokens

2. **Implemente um backend real**
   - API REST segura
   - Autenticação por JWT
   - Rate limiting

3. **Use HTTPS**
   - Nunca transmita dados sensíveis via HTTP

4. **Validação adicional**
   - Validar CPF/CNPJ
   - Verificação de email
   - 2FA para admin

---

## 🐛 Possíveis Melhorias Futuras

- [ ] Recuperação de senha
- [ ] Edição de perfil de usuário
- [ ] Relatório de vendas mais detalhado
- [ ] Múltiplos níveis de permissão
- [ ] Backup/Export de dados
- [ ] Dark mode
- [ ] Notificações
- [ ] Busca avançada de produtos

---

## 📞 Suporte

Qualquer dúvida sobre funcionamento, consulte os arquivos de código comentados ou as telas de feedback que aparecem ao usar a aplicação.

Boa sorte! 🚀

