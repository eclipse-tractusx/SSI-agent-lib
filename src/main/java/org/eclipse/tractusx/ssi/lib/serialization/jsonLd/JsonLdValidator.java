package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import foundation.identity.jsonld.JsonLDObject;

public interface JsonLdValidator {

  public boolean validate(JsonLDObject jsonLdObject);
}
