#!/bin/bash

set -x -o xtrace

helm uninstall cx

./mvnw clean spotless:apply install package -Pwith-docker-image

minikube image load cx-ssi-agent-app:latest
minikube image load lab-cx-ssi-connector-app:latest
minikube image load edc-dataplane-hashicorp-vault:latest

infraChartsDir=./charts/lab-infrastructure

helm dependency update "${infraChartsDir}"

helm upgrade --install cx "${infraChartsDir}" \
  --set bob.ssi.agent.embedded.enforceHttpsToResolveDidWeb=false \
  --set bob.controlplane.image.tag=latest \
  --set bob.controlplane.image.repository=docker.io/library/lab-cx-ssi-connector-app \
  --set bob.controlplane.image.pullPolicy=Never \
  --set bob.dataplane.image.tag=latest \
  --set alice.ssi.agent.embedded.enforceHttpsToResolveDidWeb=false \
  --set alice.controlplane.image.tag=latest \
  --set alice.controlplane.image.repository=docker.io/library/lab-cx-ssi-connector-app \
  --set alice.controlplane.image.pullPolicy=Never \
  --set alice.dataplane.image.tag=latest \
  --set operator.image.tag=latest \
  --set operator.image.repository=docker.io/library/cx-ssi-agent-app \
  --set operator.image.pullPolicy=Never

hostName=$(minikube ip)
aliceDataMgmtPort=$(kubectl get service alice-controlplane -o jsonpath='{.spec.ports[?(@.name=="data")].nodePort}')
aliceSsiPort=$(kubectl get service alice-controlplane -o jsonpath='{.spec.ports[?(@.name=="ssi")].nodePort}')
bobDataMgmtPort=$(kubectl get service bob-controlplane -o jsonpath='{.spec.ports[?(@.name=="data")].nodePort}')
bobSsiPort=$(kubectl get service bob-controlplane -o jsonpath='{.spec.ports[?(@.name=="ssi")].nodePort}')
operatorPort=$(kubectl get service operator -o jsonpath='{.spec.ports[?(@.name=="http")].nodePort}')

rm ./lab-cx-ssi-system-test/src/test/resources/bob.yaml
cat <<EOT >>./lab-cx-ssi-system-test/src/test/resources/bob.yaml
bpn: "BPNBOB"
hostName:
  external: "${hostName}"
  internal: "bob-controlplane"
ssiApi:
  port: ${bobSsiPort}
didDocumentApi:
  port: 8086
dataMgmtApi:
  port: ${bobDataMgmtPort}
  authKey: "password"
idsApi:
  port: 8084
EOT

rm ./lab-cx-ssi-system-test/src/test/resources/alice.yaml
cat <<EOT >>./lab-cx-ssi-system-test/src/test/resources/alice.yaml
bpn: "BPNALICE"
hostName:
  external: "${hostName}"
  internal: "alice-controlplane"
ssiApi:
  port: ${aliceSsiPort}
didDocumentApi:
  port: 8086
dataMgmtApi:
  port: ${aliceDataMgmtPort}
  authKey: "password"
idsApi:
  port: 8084
EOT

rm ./lab-cx-ssi-system-test/src/test/resources/operator.yaml
cat <<EOT >>./lab-cx-ssi-system-test/src/test/resources/operator.yaml
hostName: "${hostName}"
agentApi:
  port: ${operatorPort}
EOT
