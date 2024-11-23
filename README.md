# How to run Employee Management App
## Secrets 
Following secrets should be provided through env variables 
either directly while running the app or through system env vars
this could happen through k8s utilizing secret store such as AWS secret manager or terraform Vault

- MailGun secrets
```
  SPRING_MAIL_USERNAME
  SPRING_MAIL_PASSOWRD
```

- ZeroBounce secrets
```
APP_MAIL_VALIDATION_CONFIG_API_KEY
```

To populate above values please find the `keys.txt` file 

Example assume we are using aws secretemanager to store our secrets

we stored secret as below value 
```json
{
  "username": "usernamevaluec",
  "password": "dummypasswordc"
}
```

Yaml file could be 

```yaml
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: springboot-app-sa
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::YOUR_AWS_ACCOUNT_ID:role/SecretsManagerAccessRole
---
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: mail-service-secret-provider
spec:
  provider: aws
  secretObjects:
    - secretName: mail-service-secret  # Kubernetes secret name to hold the mail service credentials
      type: Opaque
      data:
        - secretKey: username
          key: SPRING_MAIL_USERNAME  # Alias for username to be used as an env variable
        - secretKey: password
          key: SPRING_MAIL_PASSWORD  # Alias for password to be used as an env variable
  parameters:
    objects: |
      - objectName: "mail-service-secret"  # Secret in AWS Secrets Manager
        objectType: "secretsmanager"
---
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: zerobounce-api-key-provider
spec:
  provider: aws
  secretObjects:
    - secretName: zerobounce-api-key-secret  # Kubernetes secret name for ZeroBounce API Key
      type: Opaque
      data:
        - secretKey: apiKey
          key: APP_MAIL_VALIDATION_CONFIG_API_KEY  # Alias for ZeroBounce API Key to be used as an env variable
  parameters:
    objects: |
      - objectName: "zerobounce-api-key-secret"  # The secret in AWS Secrets Manager for the ZeroBounce API key
        objectType: "secretsmanager"
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: springboot-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: springboot-app
  template:
    metadata:
      labels:
        app: springboot-app
    spec:
      serviceAccountName: springboot-app-sa  # Use the service account with access to Secrets Manager
      containers:
        - name: springboot-app
          image: your-docker-image  # Replace with your actual Docker image
          env:
            - name: SPRING_MAIL_USERNAME
              valueFrom:
                secretKeyRef:
                  name: mail-service-secret  # Secret name for mail service credentials
                  key: SPRING_MAIL_USERNAME  # Environment variable key for the username
            - name: SPRING_MAIL_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mail-service-secret
                  key: SPRING_MAIL_PASSWORD  # Environment variable key for the password
            - name: APP_MAIL_VALIDATION_CONFIG_API_KEY
              valueFrom:
                secretKeyRef:
                  name: zerobounce-api-key-secret  # Kubernetes secret name for ZeroBounce API key
                  key: APP_MAIL_VALIDATION_CONFIG_API_KEY  # Environment variable key for ZeroBounce API key
          volumeMounts:
            - mountPath: /mnt/secrets-store
              name: secrets-store
              readOnly: true
      volumes:
        - name: secrets-store
          csi:
            driver: secrets-store.csi.k8s.io
            readOnly: true
            volumeAttributes:
              secretProviderClass: "mail-service-secret-provider"  # Use the SecretProviderClass for mail service credentials
```
