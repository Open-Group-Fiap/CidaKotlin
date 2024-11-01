# Cida

## Pitch 

### Problema
Em um mundo cada vez mais data-driven, as empresas estão se afogando em um mar de dados não estruturados. Essa abundância de dados, muitas vezes desorganizada e sem significado, esconde insights valiosos que podem impulsionar o crescimento e a competitividade das empresas. Muitas vezes a solução ideal seria contratar uma empresa de consultoria para analisar a situação interna da empresa com base nesses dados, porém isso também vem com seus próprios problemas, como custo elevado, resultado da mão de obra qualificada que deverá realizar o serviço, também temos a imprevisibilidade da análise humana, além do tempo que será gasto com a espera da finalização dessa analise, e a incapacidade de transformar esses dados brutos em insights acionáveis representa um obstáculo significativo para o sucesso das empresas, que pode resultar em perca de competitividade, ineficiência na sua gestão interna e a não tomada das melhores decisões para o futuro da empresa.

### Nossa Solução
Com base nisso visionamos uma solução que consiga facilitar esse processo, de uma forma rápida e previsível, que pudesse utilizar do poder das IAs generativas para resolver esse problema.

Então criamos a CIDA ou (Consulting Insights with Deep Analysis), a nossa IA de processamento de dados e geração de insights empresariais, a CIDA é dividida em dois momentos, no primeiro momento ela recebe toda e qualquer documentação interna da empresa, não precisando estar em uma estrutura fixa ou padronizada, e ela tem a capacidade de processar esses dados brutos e refinar eles para a sua forma mais pura, passando assim todos esses dados refinados para o seu segundo momento, nesse ponto a IA generativa entra em ação, analisando todos esses dados e assim gerando insights e aconselhando sobre possíveis melhorias a serem feitas internamente na empresa.

A CIDA com a sua incrível capacidade de transformação de dados brutos em insights acionáveis também traz algumas vantagens a alternativa humana, primeiramente temos o custo, que é muito menor simplesmente pelo fato de não haver mão de obra envolvida, os processamentos realizados são totalmente automáticos e não necessitam de supervisão, também temos a facilidade de uso, já que você pode só fazer upload de arquivos PDF ou planilhas do Excel que já estão prontos e a parte de processar esses dados fica conosco, também temos o tempo que é muito menor às alternativas humanas tendo em vista o processo realizado por uma máquina e também temos a previsibilidade da análise, tornando ela muito mais confiável do que uma possível análise humana.
### Publico Alvo
Nós observamos 2 públicos focais para a CIDA, PMEs ou empresas de pequeno e médio porte, que não necessariamente tem uma estrutura interna de documentação totalmente regrada e padronizada, ponto esse que deixaria uma consultoria humana mais cara, e também para essas empresas muitas vezes ter uma empresa de consultoria de alto nível vindo fazer a consultoria não seria possível pelo custo elevado desses serviços, outro grande possível publico seriam startups que tem em sua natureza se movimentar e mudar rapidamente, oque também impossibilitaria uma análise aprofundada vinda de uma empresa de consultoria que deverá analisar seus processos e documentações internas, fatores esses muitas vezes indefinidos nesses momentos iniciais da vida de uma empresa.
Outros possíveis públicos são: Empreendedores, departamentos individuais dentro de empresas e empresas de consultoria usando a CIDA como uma assistente na sua análise.
### Produtos Semelhantes
Atualmente os únicos produtos similares ao nosso no mercado são ferramentas de BI como:
- **Board:** [https://www.board.com/](https://www.board.com/)
- **ThoughtSpot:** [https://www.thoughtspot.com/](https://www.thoughtspot.com/)

Porém, ambas as ferramentas têm 3 diferenças essenciais, sendo:
- Ambas exigem a entrada manual de dados
- Ambas não conseguem realizar processamento de dados brutos como conseguimos
- Ambas exigem que a empresa realize uma parceria com a plataforma de BI, por exigir todo um processo de transição e analise para esses sistemas, fator esse que não existe com a CIDA

Vale também ressaltar que ambas essas plataformas têm o foco delas em BI e administração, enquanto isso, nós temos o nosso foco apenas em fornecer uma consultoria daquele momento da empresa.

### Potencial de Mercado
Pensando nos nossos públicos alvos de PMEs e startups, atualmente 99% das empresas abertas no Brasil se classificam como PME, no ano de 2021 tinham 20.798.291 empresas abertas no Brasil, 99% disso seriam aproximadamente 20.590.308 possíveis clientes, e no mesmo ano tivemos a abertura de 3.868.687 novas empresas, então o potencial de mercado é na casa dos milhões de clientes.
Fontes:
https://www.gov.br/empresas-e-negocios/pt-br/mapa-de-empresas/boletins/mapa-de-empresas-boletim-3o-quadrimestre-2023.pdf
https://exame.com/pme/as-pmes-representam-27-do-pib-confira-dicas-para-ter-sucesso-na-sua_red-01/

### Grupo:
- Cauã Alencar Rojas Romero - RM98638
- Jaci Teixeira Santos - RM99627
- Leonardo dos Santos Guerra - RM99738
- Maria Eduarda Ferreira da Mata - RM99004

## Aplicação

### Nota Importante
O servidor hosteado na azure tem um cold boot relativamente longo, então caso a aplicação não carregue de primeira, aguarde alguns segundos e tente novamente.

### Explicação da tecnologia
Temos 6 telas, sendo elas:
- **Home:** Tela inicial, com os botões para navegar para login e registro (Caso exista um login salvo localmente essa tela não é exibida)
- **Login:** Tela de login, onde o usuário pode fazer login com um email e senha
- **Registro:** Tela de registro, onde o usuário pode se registrar com os dados empresariais
- **Upload:** Tela de upload, onde o usuário pode fazer upload de documentos para a CIDA processar
- **Dashboard:** Tela de dashboard, onde o usuário pode ver os insights gerados pela CIDA
- **Usuário:** Tela de usuário, onde o usuário pode ver e editar seus dados, apagar sua conta e fazer logout

Utilizamos o Retrofit para se comunicar com nossa api c# hosteada na azure, (Link do site)[https://cida.azurewebsites.net/], (Link do repo da api)[https://github.com/Open-Group-Fiap/CIDA-DotNet8-DevOps]

Utilizamos o SQLite para salvar os dados do usuário localmente, para que ele não precise fazer login toda vez que abrir o app

Utilizamos o Firestore para loggar erros que acontecem no app, para que possamos corrigir eles mais facilmente


