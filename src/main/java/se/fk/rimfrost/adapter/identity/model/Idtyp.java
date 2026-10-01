package se.fk.rimfrost.adapter.identity.model;

import org.immutables.value.Value;

/**
 * Identifies an individual by type and value, e.g. personnummer or samordningsnummer.
 */
@Value.Immutable
public interface Idtyp
{
   /** The identifier type, e.g. {@code PERSONNUMMER}. */
   String typId();

   /** The identifier value. */
   String varde();
}
