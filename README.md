# product-vcissuer

## About this repository


## How To

**Update Eclipse Dependencies File**
```bash
cd cx-ssi-lib
./../mvnw org.eclipse.dash:license-tool-plugin:license-check \
    -Ddash.summary=DEPENDENCIES
```

**Generate Eclipse IP Issues**
> Eclipse Commiters Only
```bash
cd cx-ssi-lib
./../mvnw org.eclipse.dash:license-tool-plugin:license-check \
  -Ddash.summary=cx-ssi-lib/DEPENDENCIES
  -Ddash.projectId=automotive.tractusx \
  -Ddash.iplab.token=<token*>
```
