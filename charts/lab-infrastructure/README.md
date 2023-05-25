# lab-infrastructure

![Version: 0.0.4](https://img.shields.io/badge/Version-0.0.4-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 0.0.4](https://img.shields.io/badge/AppVersion-0.0.4-informational?style=flat-square)

A Helm chart for Kubernetes

## Requirements

| Repository | Name | Version |
|------------|------|---------|
| file://../agent | operator(ssi-agent) | 0.0.3 |
| file://../lab-connector | bob(lab-ssi-connector) | 0.0.3 |
| file://../lab-connector | alice(lab-ssi-connector) | 0.0.3 |
| https://denisneuling.github.io/cx-backend-service | backend(backend-service) | 0.0.6 |
| https://helm.releases.hashicorp.com | vault(vault) | 0.20.0 |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| alice.backendService.httpProxyTokenReceiverUrl | string | `"http://backend:8080"` |  |
| alice.backendService.service.type | string | `"NodePort"` |  |
| alice.controlplane.debug.enabled | bool | `true` |  |
| alice.controlplane.debug.suspendOnStart | bool | `false` |  |
| alice.controlplane.endpoints.data.authKey | string | `"password"` |  |
| alice.controlplane.internationalDataSpaces.id | string | `"did:web:alice-controlplane%3A8087"` |  |
| alice.controlplane.livenessProbe.enabled | bool | `false` |  |
| alice.controlplane.readinessProbe.enabled | bool | `false` |  |
| alice.controlplane.service.type | string | `"NodePort"` |  |
| alice.dataplane.debug.enabled | bool | `true` |  |
| alice.dataplane.debug.suspendOnStart | bool | `false` |  |
| alice.fullnameOverride | string | `"alice"` |  |
| alice.ssi.agent.embedded.hostname | string | `"alice-controlplane:8086"` |  |
| alice.ssi.agent.embedded.signingKey.privateKeyVaultAlias | string | `"sokrates/ssi/private-key"` |  |
| alice.ssi.agent.embedded.signingKey.publicKeyVaultAlias | string | `"sokrates/ssi/public-key"` |  |
| alice.ssi.dataspace.operator | string | `"did:web:operator%3A8080"` |  |
| alice.vault.hashicorp.token | string | `"root"` |  |
| alice.vault.hashicorp.url | string | `"http://vault:8200"` |  |
| alice.vault.secretNames.transferProxyTokenEncryptionAesKey | string | `"sokrates/data-encryption-aes-keys"` |  |
| alice.vault.secretNames.transferProxyTokenSignerPrivateKey | string | `"sokrates/transfer-proxy-private-key"` |  |
| alice.vault.secretNames.transferProxyTokenSignerPublicKey | string | `"sokrates/transfer-proxy-public-key"` |  |
| backend.fullnameOverride | string | `"backend"` |  |
| backend.service.backend.port | int | `8081` |  |
| backend.service.frontend.port | int | `8080` |  |
| backend.service.type | string | `"NodePort"` |  |
| bob.backendService.httpProxyTokenReceiverUrl | string | `"http://backend:8080"` |  |
| bob.backendService.service.type | string | `"NodePort"` |  |
| bob.controlplane.debug.enabled | bool | `true` |  |
| bob.controlplane.debug.suspendOnStart | bool | `false` |  |
| bob.controlplane.endpoints.data.authKey | string | `"password"` |  |
| bob.controlplane.internationalDataSpaces.id | string | `"did:web:bob-controlplane%3A8086"` |  |
| bob.controlplane.livenessProbe.enabled | bool | `false` |  |
| bob.controlplane.readinessProbe.enabled | bool | `false` |  |
| bob.controlplane.service.type | string | `"NodePort"` |  |
| bob.dataplane.debug.enabled | bool | `true` |  |
| bob.dataplane.debug.suspendOnStart | bool | `false` |  |
| bob.dataplane.internationalDataSpaces.id | string | `"did:web:bob%3A8087"` |  |
| bob.fullnameOverride | string | `"bob"` |  |
| bob.ssi.agent.embedded.hostname | string | `"bob-controlplane:8086"` |  |
| bob.ssi.agent.embedded.signingKey.privateKeyVaultAlias | string | `"plato/ssi/private-key"` |  |
| bob.ssi.agent.embedded.signingKey.publicKeyVaultAlias | string | `"plato/ssi/public-key"` |  |
| bob.ssi.dataspace.operator | string | `"did:web:operator%3A8080"` |  |
| bob.vault.hashicorp.token | string | `"root"` |  |
| bob.vault.hashicorp.url | string | `"http://vault:8200"` |  |
| bob.vault.secretNames.transferProxyTokenEncryptionAesKey | string | `"plato/data-encryption-aes-keys"` |  |
| bob.vault.secretNames.transferProxyTokenSignerPrivateKey | string | `"plato/transfer-proxy-private-key"` |  |
| bob.vault.secretNames.transferProxyTokenSignerPublicKey | string | `"plato/transfer-proxy-public-key"` |  |
| install.backendservice | bool | `true` |  |
| install.vault | bool | `true` |  |
| operator.app.host | string | `"operator:8080"` |  |
| operator.fullnameOverride | string | `"operator"` |  |
| operator.service.type | string | `"NodePort"` |  |
| vault.fullnameOverride | string | `"vault"` |  |
| vault.injector.enabled | bool | `false` |  |
| vault.server.dev.devRootToken | string | `"root"` |  |
| vault.server.dev.enabled | bool | `true` |  |
| vault.server.postStart[0] | string | `"sh"` |  |
| vault.server.postStart[1] | string | `"-c"` |  |
| vault.server.postStart[2] | string | `"{ \n\nsleep 10\n\n/bin/vault kv put secret/plato/data-encryption-aes-keys content=H7j47H6vVQQOv/hbdAYz+w==\n\ncat << EOF | /bin/vault kv put secret/plato/ssi/private-key content=-\n-----BEGIN PRIVATE KEY-----\nMC4CAQAwBQYDK2VwBCIEIM+4v1LvvZLaeqTytvL7VSGwrH2LG8D21zFoTkfloh2c\n-----END PRIVATE KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/plato/ssi/public-key content=-\n-----BEGIN PUBLIC KEY-----\nMCowBQYDK2VwAyEAUtvRSBZ5Z3SDrCJ24Pb+gOUJJWvkSdkVZqS8z/mWfSg=\n-----END PUBLIC KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/sokrates/transfer-proxy-private-key content=-\n-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbX9S8de5FTWl4\nQYdQtkE/yPyCitcBC9TPxmdvWJU22V0jb0YJJeRMfGmHrrFNx6B0Xxp5qhTWY9/c\nvMxBRTWShkdSrqxEL7nS4VxknYCMXkhcpCUM81ZHm09c/npFnKpfGM8Ud65MD5ix\njKvMRWHMSJ46I1rEkMze5Xmm/4VNEfP7Ew6A0GidHVDIyHq1zAH4RBqCh4WXQOMR\ncB6X1YbiE4b+sOJcCtMxpWOdMb/JyYXQd5KdtEL7f/5+H8KHNAnIRu+JfrvKbJhE\nlPLKe38y2uqDn+yx0hJ7GPDdmaIU16Zkg6Eh16mhLCckcUzgmPwHmWrRrWNjlFz2\nUOPvXRFdAgMBAAECggEAN2yd5IRk9I/CucUWUfJRoEE/4glI3PSte1iY+R0uTRyI\nnuVIpGbB447VzjLAyLAXSqvKM/A58qg56PHoIrhffd8sfhAVH1WvAcymOrX8bxYK\n1hEvrkj3VB/Q1alpUH+sPrQI2pI+uJ8vptY5SmrNkiOtXavS6x+EFVbiaHHpyS26\nASaCoRpdBoNTm0SAiDBTK6MqTs4vRpqKseGdC76F+jKimYrTJY19ZctSIAMjrnqd\nqzRL+jfob5vMqKC22AjInkZ8BZWll1ZoTnv37bq2NAb9lvdY73REm42Wpm5S7PET\nEixe69gvi/IwaSe27S36+kcrQoYHnxbb31+Xt+0pQQKBgQDJfA2ZnYmcA3yvVQhi\ne76I3rq6AEfcG4EDhf+JRO2QHKMMXLwfFAdSR8QflxNUWy1y6q/783EpgLJ1Kv8h\nuNkTH6JyV7kFhwfvxWreAWx2jRQRACqnuaLnJ/28vd8Il0kc3/BQsWzbg6YTERrq\n0Au2RW/c9blrKS0MyurtOtZsiQKBgQDFaezSCWUspeNci5lrdvMiHBLOUgR2guQm\nGtf9RdBmzvtBqpdkP8AEMhRW7oSGcKpDldd0Klyml7s/CDYTL7sflHtKRiTQmWuJ\n+p3uvyylAxr/Swfw56hj5Y4/Oj2CLIuUlglewo40JnvvM5icT7RGvbyaIIhYzIsR\nHTv3t8eRNQKBgA4l8eaJk3IrJIRDWlVgDx8ZVM9e2azxGXwf2rPO7UejWyexE1yz\nUVhLxc/aEfdod6aMKFNu4tFhQibMICJEEqovHH8e/dUPiFUj7b8tJmqkuXYAJv6k\nIHZO7phkVNcLmIy4hO2Fp/k6I11PZC588XWZJqPDdYO63nj5fsmtygTRAoGBAJ72\nYH/wmMuO+Ll4n51tNvJscKg6WuWjGFumme2T3fArEx8ZYraSruex+7bUcVpgNnod\nmlQsGFb9LwXecsyYTrFrOqvgN5zRLUr5x1qMDkMBcSfJHyfZIjruidBX8Vd0zyBi\ngEERoLhVlM5UWbrkY2HjPo9NSv1WF1U8mSErl0NRAoGAYC3RxEfGxD9+Qi08nQgg\ns/48hLdD2k2q4t3FrDsIGPAIEs52CGp9JWil9RyIQxBXWejETwDz+PgmD6U86Mhh\nQf5css6pcP/w1XF8vsyXfPnecgPSyOE4CgLtnQLxNriMiy5pfALELLyxoBQ+nquz\nfMNLPC4K85ps/Uu9uzSatl0=\n-----END PRIVATE KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/sokrates/transfer-proxy-public-key content=-\n-----BEGIN CERTIFICATE-----\nMIID7TCCAtWgAwIBAgIUJ0bwxUc7n3YK89mOyGXrLx2KO0YwDQYJKoZIhvcNAQEL\nBQAwgYUxCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJl\ncmxpbjEMMAoGA1UECgwDQk1XMSAwHgYDVQQLDBdlZGMtcGxheWdyb3VuZC1wYXJ0\nbmVyMjEkMCIGA1UEAwwbcGxhdG8tZWRjLmRlbW8uY2F0ZW5hLXgubmV0MB4XDTIy\nMDQyNTEwMjgwMloXDTIzMDQyNTEwMjgwMlowgYUxCzAJBgNVBAYTAkRFMQ8wDQYD\nVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJlcmxpbjEMMAoGA1UECgwDQk1XMSAwHgYD\nVQQLDBdlZGMtcGxheWdyb3VuZC1wYXJ0bmVyMjEkMCIGA1UEAwwbcGxhdG8tZWRj\nLmRlbW8uY2F0ZW5hLXgubmV0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\nAQEAm1/UvHXuRU1peEGHULZBP8j8gorXAQvUz8Znb1iVNtldI29GCSXkTHxph66x\nTcegdF8aeaoU1mPf3LzMQUU1koZHUq6sRC+50uFcZJ2AjF5IXKQlDPNWR5tPXP56\nRZyqXxjPFHeuTA+YsYyrzEVhzEieOiNaxJDM3uV5pv+FTRHz+xMOgNBonR1QyMh6\ntcwB+EQagoeFl0DjEXAel9WG4hOG/rDiXArTMaVjnTG/ycmF0HeSnbRC+3/+fh/C\nhzQJyEbviX67ymyYRJTyynt/Mtrqg5/ssdISexjw3ZmiFNemZIOhIdepoSwnJHFM\n4Jj8B5lq0a1jY5Rc9lDj710RXQIDAQABo1MwUTAdBgNVHQ4EFgQUmYOnF4b/mJPO\noN2h8Tb69g91CiMwHwYDVR0jBBgwFoAUmYOnF4b/mJPOoN2h8Tb69g91CiMwDwYD\nVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAKxv/MTIEKNkzReOqrzpt\nLM00X6JsDdfxa3rZ0Uq17PjO0R63IPsqzexhfZUML0e/Dwpe97xpvftCOEuICMBA\nwOHhQc77MgwyF4dqgRgfJysxw37ACZxU6GI/K2JpKXQLgEhP14oHUIWOzCAbgDhR\njwOx3ZP176Vjxx90pW3hOphRVnq/BRqqEDtFwRzKtGnGvP8ecmC2iY4dXEA3QEp1\ngzg03eglvZSoedEPY5o5y/4n6TplaDmaeoo0QrvAiWik1gY85Lg21aBWVsP45wVS\ntFn3m1FCCV8XYIj/EEUAh8VEhphLVEViE6m9Mm4deFDavXcGBb63BCiOQtnjd3eY\nzQ==\n-----END CERTIFICATE-----\nEOF\n\n/bin/vault kv put secret/sokrates/data-encryption-aes-keys content=OcvxzWCK8ETSjt1jmZw3RA==\n\ncat << EOF | /bin/vault kv put secret/sokrates/ssi/private-key content=-\n-----BEGIN PRIVATE KEY-----\nMC4CAQAwBQYDK2VwBCIEIFgZFKsyHZ0yA9xkroZPB8NTIl1dopnX9nhH2q6puEK8\n-----END PRIVATE KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/sokrates/ssi/public-key content=-\n-----BEGIN PUBLIC KEY-----\nMCowBQYDK2VwAyEABqAmUe/amV/nAVUt01XyrLpmQLOyLqF6LnAkH4QdyqI=\n-----END PUBLIC KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/plato/transfer-proxy-private-key content=-\n-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbX9S8de5FTWl4\nQYdQtkE/yPyCitcBC9TPxmdvWJU22V0jb0YJJeRMfGmHrrFNx6B0Xxp5qhTWY9/c\nvMxBRTWShkdSrqxEL7nS4VxknYCMXkhcpCUM81ZHm09c/npFnKpfGM8Ud65MD5ix\njKvMRWHMSJ46I1rEkMze5Xmm/4VNEfP7Ew6A0GidHVDIyHq1zAH4RBqCh4WXQOMR\ncB6X1YbiE4b+sOJcCtMxpWOdMb/JyYXQd5KdtEL7f/5+H8KHNAnIRu+JfrvKbJhE\nlPLKe38y2uqDn+yx0hJ7GPDdmaIU16Zkg6Eh16mhLCckcUzgmPwHmWrRrWNjlFz2\nUOPvXRFdAgMBAAECggEAN2yd5IRk9I/CucUWUfJRoEE/4glI3PSte1iY+R0uTRyI\nnuVIpGbB447VzjLAyLAXSqvKM/A58qg56PHoIrhffd8sfhAVH1WvAcymOrX8bxYK\n1hEvrkj3VB/Q1alpUH+sPrQI2pI+uJ8vptY5SmrNkiOtXavS6x+EFVbiaHHpyS26\nASaCoRpdBoNTm0SAiDBTK6MqTs4vRpqKseGdC76F+jKimYrTJY19ZctSIAMjrnqd\nqzRL+jfob5vMqKC22AjInkZ8BZWll1ZoTnv37bq2NAb9lvdY73REm42Wpm5S7PET\nEixe69gvi/IwaSe27S36+kcrQoYHnxbb31+Xt+0pQQKBgQDJfA2ZnYmcA3yvVQhi\ne76I3rq6AEfcG4EDhf+JRO2QHKMMXLwfFAdSR8QflxNUWy1y6q/783EpgLJ1Kv8h\nuNkTH6JyV7kFhwfvxWreAWx2jRQRACqnuaLnJ/28vd8Il0kc3/BQsWzbg6YTERrq\n0Au2RW/c9blrKS0MyurtOtZsiQKBgQDFaezSCWUspeNci5lrdvMiHBLOUgR2guQm\nGtf9RdBmzvtBqpdkP8AEMhRW7oSGcKpDldd0Klyml7s/CDYTL7sflHtKRiTQmWuJ\n+p3uvyylAxr/Swfw56hj5Y4/Oj2CLIuUlglewo40JnvvM5icT7RGvbyaIIhYzIsR\nHTv3t8eRNQKBgA4l8eaJk3IrJIRDWlVgDx8ZVM9e2azxGXwf2rPO7UejWyexE1yz\nUVhLxc/aEfdod6aMKFNu4tFhQibMICJEEqovHH8e/dUPiFUj7b8tJmqkuXYAJv6k\nIHZO7phkVNcLmIy4hO2Fp/k6I11PZC588XWZJqPDdYO63nj5fsmtygTRAoGBAJ72\nYH/wmMuO+Ll4n51tNvJscKg6WuWjGFumme2T3fArEx8ZYraSruex+7bUcVpgNnod\nmlQsGFb9LwXecsyYTrFrOqvgN5zRLUr5x1qMDkMBcSfJHyfZIjruidBX8Vd0zyBi\ngEERoLhVlM5UWbrkY2HjPo9NSv1WF1U8mSErl0NRAoGAYC3RxEfGxD9+Qi08nQgg\ns/48hLdD2k2q4t3FrDsIGPAIEs52CGp9JWil9RyIQxBXWejETwDz+PgmD6U86Mhh\nQf5css6pcP/w1XF8vsyXfPnecgPSyOE4CgLtnQLxNriMiy5pfALELLyxoBQ+nquz\nfMNLPC4K85ps/Uu9uzSatl0=\n-----END PRIVATE KEY-----\nEOF\n\ncat << EOF | /bin/vault kv put secret/plato/transfer-proxy-public-key content=-\n-----BEGIN CERTIFICATE-----\nMIID7TCCAtWgAwIBAgIUJ0bwxUc7n3YK89mOyGXrLx2KO0YwDQYJKoZIhvcNAQEL\nBQAwgYUxCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJl\ncmxpbjEMMAoGA1UECgwDQk1XMSAwHgYDVQQLDBdlZGMtcGxheWdyb3VuZC1wYXJ0\nbmVyMjEkMCIGA1UEAwwbcGxhdG8tZWRjLmRlbW8uY2F0ZW5hLXgubmV0MB4XDTIy\nMDQyNTEwMjgwMloXDTIzMDQyNTEwMjgwMlowgYUxCzAJBgNVBAYTAkRFMQ8wDQYD\nVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJlcmxpbjEMMAoGA1UECgwDQk1XMSAwHgYD\nVQQLDBdlZGMtcGxheWdyb3VuZC1wYXJ0bmVyMjEkMCIGA1UEAwwbcGxhdG8tZWRj\nLmRlbW8uY2F0ZW5hLXgubmV0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\nAQEAm1/UvHXuRU1peEGHULZBP8j8gorXAQvUz8Znb1iVNtldI29GCSXkTHxph66x\nTcegdF8aeaoU1mPf3LzMQUU1koZHUq6sRC+50uFcZJ2AjF5IXKQlDPNWR5tPXP56\nRZyqXxjPFHeuTA+YsYyrzEVhzEieOiNaxJDM3uV5pv+FTRHz+xMOgNBonR1QyMh6\ntcwB+EQagoeFl0DjEXAel9WG4hOG/rDiXArTMaVjnTG/ycmF0HeSnbRC+3/+fh/C\nhzQJyEbviX67ymyYRJTyynt/Mtrqg5/ssdISexjw3ZmiFNemZIOhIdepoSwnJHFM\n4Jj8B5lq0a1jY5Rc9lDj710RXQIDAQABo1MwUTAdBgNVHQ4EFgQUmYOnF4b/mJPO\noN2h8Tb69g91CiMwHwYDVR0jBBgwFoAUmYOnF4b/mJPOoN2h8Tb69g91CiMwDwYD\nVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAKxv/MTIEKNkzReOqrzpt\nLM00X6JsDdfxa3rZ0Uq17PjO0R63IPsqzexhfZUML0e/Dwpe97xpvftCOEuICMBA\nwOHhQc77MgwyF4dqgRgfJysxw37ACZxU6GI/K2JpKXQLgEhP14oHUIWOzCAbgDhR\njwOx3ZP176Vjxx90pW3hOphRVnq/BRqqEDtFwRzKtGnGvP8ecmC2iY4dXEA3QEp1\ngzg03eglvZSoedEPY5o5y/4n6TplaDmaeoo0QrvAiWik1gY85Lg21aBWVsP45wVS\ntFn3m1FCCV8XYIj/EEUAh8VEhphLVEViE6m9Mm4deFDavXcGBb63BCiOQtnjd3eY\nzQ==\n-----END CERTIFICATE-----\nEOF\n\n}\n"` |  |
| vault.ui.enabled | bool | `true` |  |
| vault.ui.externalPort | int | `8200` |  |
| vault.ui.targetPort | int | `8200` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.10.0](https://github.com/norwoodj/helm-docs/releases/v1.10.0)
