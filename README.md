# API Associação Okinawa

## Autenticação e JWT

`POST /auth/login` autentica e-mail e senha com BCrypt e retorna um JWT HS256.
O token contém o ID do usuário (`sub`), o emissor (`iss`), a data de emissão (`iat`)
e a expiração (`exp`). A validade configurada é de 15 minutos (900 segundos).

Nas rotas protegidas, envie `Authorization: Bearer <token>`. A API valida a assinatura,
o emissor e a validade; em seguida consulta o usuário e suas permissões no banco.
Usuários removidos, desativados ou sem função não podem usar o token. A API não usa
sessão e não possui refresh token: ao expirar, faça login novamente.

Os endpoints `/usuarios` exigem a funcionalidade `TELA_USUARIOS` na função do usuário.
O Swagger e o login são públicos; os demais endpoints exigem autenticação.

## Preparar o ambiente local

Requisitos: JDK 21, PostgreSQL acessível e um banco já criado. O Hibernate está
configurado com `ddl-auto: update` e cria/atualiza as tabelas ao iniciar a aplicação.

Se ainda não existir `.env`, copie `.env.example` para `.env`. Preencha sem aspas:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/associacao_okinawa
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha_do_banco
JWT_SECRET=cole_a_chave_base64_gerada_abaixo
SPRING_PROFILES_ACTIVE=dev
APP_BOOTSTRAP_ENABLED=true
APP_BOOTSTRAP_EMAIL=swagger@example.com
APP_BOOTSTRAP_PASSWORD=defina_uma_senha_local_com_pelo_menos_8_caracteres
```

Gere uma chave aleatória de 32 bytes em Base64 no PowerShell e copie o resultado
para `JWT_SECRET` do `.env`:

```powershell
$chaveJwt = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($chaveJwt)
[Convert]::ToBase64String($chaveJwt)
```

A aplicação não importa `.env` automaticamente. Configure as variáveis na IDE ou
importe o arquivo no terminal PowerShell que executará o Maven:

```powershell
Get-Content .env | ForEach-Object {
    $linha = $_.Trim()
    if ($linha -and -not $linha.StartsWith('#')) {
        $partes = $linha -split '=', 2
        if ($partes.Count -eq 2) {
            [Environment]::SetEnvironmentVariable($partes[0].Trim(), $partes[1].Trim(), 'Process')
        }
    }
}
.\mvnw.cmd spring-boot:run
```

`java -version` deve indicar o JDK 21; configure `JAVA_HOME` e o `PATH` se necessário.
O `.env` está ignorado pelo Git.

## Primeiro usuário de teste

A carga inicial só executa quando **o perfil `dev` e `APP_BOOTSTRAP_ENABLED=true`**
estão ativos. Ela cria um usuário ativo com o e-mail e a senha informados, armazena
a senha em BCrypt e associa uma função com `TELA_USUARIOS`. Também preenche telefone
e data de nascimento fictícios e cria uma família com endereço de teste para a conta,
evitando valores nulos nesses campos obrigatórios do banco.

Se o e-mail já estiver cadastrado, a carga inicial preserva a conta, a senha e as
permissões. Ela não recupera senhas nem reativa usuários. Depois da primeira criação,
defina `APP_BOOTSTRAP_ENABLED=false` no `.env` e importe as variáveis novamente antes
de reiniciar. A conta continua salva no banco.

## Testar pelo Swagger

1. Abra [Swagger UI](http://localhost:8080/swagger-ui/index.html).
2. Em `POST /auth/login`, clique em **Try it out** e informe o e-mail e a senha
   definidos para o usuário de teste:

   ```json
   {
     "email": "swagger@example.com",
     "senha": "a_senha_que_voce_definiu"
   }
   ```

3. Execute e copie o campo **`accesToken`** da resposta. Esse é o nome atual do campo
   no contrato da API.
4. Clique em **Authorize**, cole somente o JWT (sem `Bearer `) e confirme.
   O Swagger acrescenta o prefixo ao enviar as requisições.
5. Execute `GET /usuarios` ou os demais endpoints protegidos.

| Resposta | O que verificar |
| --- | --- |
| `200` no login | Token gerado com sucesso. |
| `400` no login | E-mail inválido ou campos obrigatórios vazios. |
| `401` no login | E-mail/senha incorretos ou usuário desativado. |
| `401` em rota protegida | Token ausente, inválido, expirado ou usuário sem acesso válido. |
| `403` em `/usuarios` | Usuário autenticado sem a permissão `TELA_USUARIOS`. |

O cadastro normal de usuários gera uma senha aleatória e atualmente não implementa
sua entrega nem recuperação. Para o primeiro acesso local, utilize a carga inicial.

## Verificar a autenticação sem banco

```powershell
.\mvnw.cmd "-Dtest=AutenticacaoSwaggerTests,UsuarioTesteInitializerTests" test
```

Os testes de autenticação usam a configuração real de segurança e JWT, com
repositórios simulados. O teste original `contextLoads` precisa das variáveis de
ambiente e do PostgreSQL para iniciar o contexto completo.
