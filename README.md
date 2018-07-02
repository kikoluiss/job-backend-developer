# Loginapp - Documentação

Aplicação de Login utilizando a especificação OAUTH2, MongoDB, cache Redis e gerador de documentação automática Swagger.

Esta aplicação opera como servidor de Autenticação e de Recursos ao mesmo tempo.

Existem três rotas expostas:
- /oauth/token (utilizada para autenticação);
- /home (página inicial - protegida); e
- /swagger-ui.html (documentação da api - não protegida)

## Exemplos de Acesso:

### Autenticação:

```shell
curl --request POST \
     --url http://host:port/gix-accounts/oauth/token \
     --header 'Authorization: Basic bXktdHJ1c3RlZC1jbGllbnQ6MTIzNDU2' \
     --header 'Cache-Control: no-cache' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data 'grant_type=password&username=user1&password=teste123'
```

### Acesso a rota protegida:

```shell
curl --request GET \
     --url http://host:port/home \
     --header 'Authorization: Bearer token' \
     --header 'Cache-Control: no-cache' \
     --header 'Content-Type: application/json' \
```

### Acesso a documentação

```shell
http://host:port/swagger-ui.html
```

## Containers e orquestração:

A aplicação está preparada para a utilização do Docker para criação de containers e Kubernetes para orquestração de pods, replicas e serviços.

O deploy deverá ser feito oberservando os seguintes requisitos:

### MongoDB

O MongoDB foi utilizado como SGDB padrão para esta aplicação, deste modo é mandatório a existência de uma instância ativa do mesmo.

Esta instância poderá ser criada no cluster do Kubernetes que estiver sendo utilizada (p.ex: Minikube) executando os respectivos yaml existentes na pasta /minikube-yamls.

Deverão ser criados, nesta ordem, o volume que conterá o banco de dados, o controlador e o serviço:
```shell
kubectl create -f mongo-volume.yaml
kubectl create -f mongo-controller.yaml
kubectl create -f mongo-service.yaml
```
Para visualizar se as instâncias foram criadas corretamente, pode-se utilizar o Dashboard ou os comandos de listagem do 
Kubernetes
```shell
minikube dashboard
```
ou
```shell
kubectl get all
```

### Redis

O Redis foi utilizado como servidor de Cache para esta aplicação, entretanto sua utilização não é mandatória, uma vez que a aplicação possui um fallback que direciona a requisição diretamente para o SGDB caso o cache não esteja acessível.

Para criar uma instância do Redis no cluster do Kubernetes, basta executar os respectivos yaml existentes na pasta /minikube-yamls.

A ordem de execução também é importante, pois existem dependências que podem gerar erros:
```shell
kubectl create -f redis-master-deployment.yaml
kubectl create -f redis-slave-deployment.yaml
kubectl create -f redis-master-service.yaml
kubectl create -f redis-slave-service.yaml
```
Para visualizar se as instâncias foram criadas corretamente, pode-se utilizar o Dashboard ou os comandos de listagem do 
Kubernetes
```shell
minikube dashboard
```
ou
```shell
kubectl get all
```

### Deploy da aplicação

O deploy da aplicação poderá ser feito utilizando uma imagem precompilada e disponível no Docker Hub, entretanto algumas configurações deverão ser feitas no arquivo '/minikube-yamls/backend-deployment.yaml' antes da criação.

Deverão ser preenchidos as variáveis de ambiente necessárias para a execução do Pod.

Sugiro que seja utilizado o comando 'kubectl get services' que irá listar todos os serviços ativos no cluster

Por exemplo:
```shell
NAME                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes                   ClusterIP   10.96.0.1       <none>        443/TCP          7d
mongo                        ClusterIP   10.97.205.55    <none>        27017/TCP        4d
redis-master                 ClusterIP   10.106.30.173   <none>        6379/TCP         7d
redis-slave                  ClusterIP   10.103.74.9     <none>        6379/TCP         7d
```

Com esta informação, o yaml de deploy poderá ser preenchido corretamente:

```shell
env:
- name: SERVER_PORT
value: "8080"
- name: MONGODB_HOST
value: "10.97.205.55"
- name: MONGODB_PORT
value: "27017"
- name: MONGODB_DATABASE
value: "loginapp"
- name: REDIS_HOST
value: "10.106.30.173"
- name: REDIS_PORT
value: "6379"
```

Após a edição do arquivo yaml, o deploy poderá ser executado e o serviço criado:

```shell
kubectl create -f backend-deployment.yaml
kubectl create -f backend-service.yaml
```

Finalmente, quando executado o comando 'kubectl get services', teremos a listagem de todos os serviços em execução neste cluster:

```shell
NAME                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
loginapp                     NodePort    10.96.197.245   <none>        8090:30056/TCP   6d
kubernetes                   ClusterIP   10.96.0.1       <none>        443/TCP          7d
mongo                        ClusterIP   10.97.205.55    <none>        27017/TCP        4d
redis-master                 ClusterIP   10.106.30.173   <none>        6379/TCP         7d
redis-slave                  ClusterIP   10.103.74.9     <none>        6379/TCP         7d
```

Para acessar a aplicação, é necessário saber qual o IP da instância do Kubernetes que está sendo executada. No caso do Minikube, basta executar o seguinte comando 'minikube dashboard --url'.

A aplicação estará disponível no IP do cluster, utilizando a porta descrita na listagem para o serviço 'loginapp':

Para acessar a documentação, deverá ser utlizado este endereço (IP e Porta podem variar conforme o deploy):
```shell
http://[IP Cluster]:30056/swagger-ui.html
```

### Conclusão

Este breve documentação trata de cobrir o funcionamento básico, as principais características e a forma de implantação da aplicação 'loginapp', em resposta ao desafio proposto pela Bluepixel.

Para mais informações, fico à disposição. Muito obrigado.
