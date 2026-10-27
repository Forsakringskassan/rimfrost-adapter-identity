# Krav — Identity Adapter

## Bakgrund

Adaptern kapslar in anrop till identity-tjänsten och exponerar dess `identity`-endpoint
som ett typat Java-API för konsumenter inom Rimfrost.

---

## Funktionella krav

### IDENT-FR-01 - Hämta identitet

- **IDENT-FR-01.1** Adaptern ska returnera hämtad identitet som en instans av `se.fk.rimfrost.adapter.identity.model.Idtyp`.
- **IDENT-FR-01.2** Om identity-tjänsten svarar med HTTP 400 ska adaptern kasta ett `IdentityException` med `ErrorType.BAD_REQUEST`.
- **IDENT-FR-01.3** Om identity-tjänsten svarar med HTTP 401 ska adaptern kasta ett `IdentityException` med `ErrorType.UNAUTHORIZED`.
- **IDENT-FR-01.4** Om identity-tjänsten svarar med HTTP 404 ska adaptern kasta ett `IdentityException` med `ErrorType.NOT_FOUND`.
- **IDENT-FR-01.5** Om identity-tjänsten svarar med HTTP 503 ska adaptern kasta ett `IdentityException` med `ErrorType.SERVICE_UNAVAILABLE`.
- **IDENT-FR-01.6** Vid övriga HTTP-fel eller kommunikationsfel ska adaptern kasta ett `IdentityException` med `ErrorType.UNEXPECTED_ERROR`.
- **IDENT-FR-01.7** Adaptern ska inte skicka `Authorized`-header ifall det givna header-värdet är null.