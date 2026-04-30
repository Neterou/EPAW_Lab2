# 🚀 Lab 2 - EPAW (Formularis i Validació)

Aquesta és la plantilla base per a la Pràctica 2 de l'assignatura d'EPAW. Està configurada perquè puguis començar a programar el teu formulari de registre de manera segura i robusta.

---

## 📖 Manual d'Instruccions
Si ets alumne de l'assignatura, hem preparat una guia detallada que t'explicarà tot el necessari pas a pas:

👉 **[LLEGIR LA GUIA DE L'ESTUDIANT](GUIA_ESTUDIANT.md)** 👈

---

## 🛠️ Com començar?

1. **Obre el terminal** a la carpeta arrel del projecte.
2. **Executa el servidor**:
   ```bash
   mvn jetty:run
   ```
3. **Accedeix a l'aplicació**:
   Obre el teu navegador a [http://localhost:8080](http://localhost:8080)
4. **Base de dades SQLite**:
   L'aplicació utilitza SQLite integrat i crea el fitxer repo-local `src/main/webapp/WEB-INF/users.db` quan s'executa.
   Això garanteix que la base de dades es troba dins del projecte i no en un directori temporal aleatori.
   Pots obrir el fitxer amb un client SQLite per comprovar les dades registrades.

---

## ✨ Avantatges d'aquesta Plantilla
- **Hot-Reload**: Els servlets es recarreguen sols al desar.
- **SQLite Integrat**: Base de dades a punt sense instal·lacions complexes.
- **Snippets**: Dreceres a VS Code per a HTML i Servlets.

---

*Creat per al laboratori d'EPAW • Universitat Pompeu Fabra*
