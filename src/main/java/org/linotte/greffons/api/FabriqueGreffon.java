package org.linotte.greffons.api;

public interface FabriqueGreffon {

	Greffon produire(String id, String classe);

}
