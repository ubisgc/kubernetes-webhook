#start the tools image
In tools image where cdssl is installet run
In power shell cd to kubernetes-webhoot root folder.

docker run -it --rm --net host -v ${HOME}/.kube/:/root/.kube/ -v ${PWD}:/work -w /work kind/tools sh

#generate certificate in /tmp
cfssl gencert -initca ./tls/ca-csr.json | cfssljson -bare /tmp/ca

cfssl gencert \
-ca=/tmp/ca.pem \
-ca-key=/tmp/ca-key.pem \
-config=./tls/ca-config.json \
-hostname="example-webhook,example-webhook.default.svc.cluster.local,example-webhook.default.svc,localhost,127.0.0.1" \
-profile=default \
./tls/ca-csr.json | cfssljson -bare /tmp/example-webhook

#make a secret
cat <<EOF > ./deploy/example-webhook-tls.yaml
apiVersion: v1
kind: Secret
metadata:
name: example-webhook-tls
type: Opaque
data:
tls.crt: $(cat /tmp/example-webhook.pem | base64 | tr -d '\n')
tls.key: $(cat /tmp/example-webhook-key.pem | base64 | tr -d '\n')
EOF

#generate CA Bundle + inject into template
ca_pem_b64="$(openssl base64 -A <"/tmp/ca.pem")"

sed -e 's@${CA_PEM_B64}@'"$ca_pem_b64"'@g' <"./deploy/webhook-template.yaml" \
> ./deploy/webhook.yaml

openssl x509 -in /tmp/example-webhook.pem -out /tmp/example-webhook.cer -inform pem -outform der
keytool -genkey -keyalg RSA -alias webhook -keystore ./src/main/resources/keystore.jks -storepass mypassword  -file /tmp/example-webhook.cer

What is your first and last name?
[sgc]:  sgc
What is the name of your organizational unit?
[Example]:  CA
What is the name of your organization?
[CA]:  Example
What is the name of your City or Locality?
[HEJLS]:
What is the name of your State or Province?
[Example]:
What is the two-letter country code for this unit?
[DK]:
Is CN=sgc, OU=CA, O=Example, L=HEJLS, ST=Example, C=DK correct?
[no]:  yes
