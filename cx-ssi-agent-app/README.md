# App

# Agent Signature

1. Generate Keys
```shell
openssl genpkey -algorithm ed25519 -out private.pem
openssl pkey -in private.pem -pubout -out public.pem
```

2. Configure Keys
```properties
private.key=./private.pem
public.key=./public.pem
```

