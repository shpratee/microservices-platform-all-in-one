#########################
# Microservies platform #
#########################

#git clone https://github.com/shpratee/microservices-platform-all-in-one.git
#cd microservices-platform-all-in-one
#git pull

####################
# Create a Cluster #
####################

minikube config set memory 10240
minikube config set cpus 4
minikube config set vm-driver virtualbox
minikube start

#############################
# Deploy Ingress Controller #
#############################

minikube addons enable ingress

kubectl --namespace kube-system wait \
    --for=condition=ready pod \
    --selector=app.kubernetes.io/component=controller \
    --timeout=120s

export INGRESS_HOST=$(minikube ip)

#######################
# Observability Stack #
#######################

# Elasticsearch

kubectl create namespace observability

cd elastic
helm repo add elastic https://helm.elastic.co

helm upgrade --install elasticsearch elastic/elasticsearch --namespace observability --create-namespace --values values.yaml --wait

# Fluent-bit

cd ../fluent-bit
helm repo add stable https://charts.helm.sh/stable

helm upgrade --install fluent-bit stable/fluent-bit --namespace observability --create-namespace --values values.yaml --wait

# Kibana

cd ../kibana
helm upgrade --install kibana elastic/kibana --namespace observability --values=values.yaml --wait

# Jaeger Tracing

cd ../jaeger/charts/charts/jaeger
helm repo add jaegertracing https://jaegertracing.github.io/helm-charts

helm upgrade --install jaeger jaegertracing/jaeger --namespace observability --values=values.yaml --wait

##############
# Monitoring #
##############

# Prometheus and Grafana

kubectl create namespace monitoring

cd ../../../prometheus/charts/charts/kube-prometheus-stack
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack --namespace monitoring --values=values.yaml --wait

##############
# PostgreSQL #
##############

kubectl create namespace postgresql

cd ../../../../postgresql
helm repo add bitnami https://charts.bitnami.com/bitnami

helm upgrade --install postgresql bitnami/postgresql --namespace postgresql --values=values.yaml --wait

#############
# Hazelcast #
#############

kubectl create namespace hazelcast

cd ../hazelcast/charts/hazelcast
helm repo add hazelcast https://hazelcast-charts.s3.amazonaws.com/

helm upgrade --install hazelcast hazelcast/hazelcast --namespace hazelcast --values=values.yaml --wait

###################
# Hashicorp Vault #
###################

kubectl create namespace vault

cd ../../../vault/charts
helm repo add hashicorp https://helm.releases.hashicorp.com

helm upgrade --install vault hashicorp/vault --namespace vault --values=values.yaml --wait

#############
# Dashboard #
#############
minikube dashboard

#######################
# Destroy The Cluster #
#######################

#minikube delete
