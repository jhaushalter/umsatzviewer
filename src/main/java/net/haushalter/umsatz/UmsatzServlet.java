// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package net.haushalter.umsatz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UmsatzServlet extends HttpServlet {
   private static final Map<String, String> VIEW_QUERIES;

   static {
      Map<String, String> queries = new LinkedHashMap<>();
      queries.put("Umsatzdetails", "SELECT * FROM banking.\"Umsatzdetails\" ORDER BY ID DESC");
      queries.put("Stammdaten", "SELECT * FROM banking.\"Stammdaten\"");
      queries.put("TOP_Empf\u00e4nger", "SELECT * FROM banking.\"TOP_Empf\u00e4nger\"");
      queries.put("Einsatzentgelt_Jahr", "SELECT * FROM banking.\"Einsatzentgelt_Jahr\"");
      queries.put("Purposecodes", "SELECT * FROM banking.\"Purposecodes\"");
      queries.put("Jahreseinkommen", "SELECT * FROM banking.\"Jahreseinkommen\"");
      VIEW_QUERIES = Collections.unmodifiableMap(queries);
   }

   public UmsatzServlet() {
   }

   protected void doGet(HttpServletRequest var1, HttpServletResponse var2) throws IOException {
      var2.setContentType("application/json; charset=UTF-8");
      String view = var1.getParameter("view");
      if (view == null || view.isEmpty()) {
         view = "Umsatzdetails";
      }

      String sql = VIEW_QUERIES.get(view);
      if (sql == null) {
         var2.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unbekannte Datenbankansicht");
         return;
      }

      String var3 = System.getProperty("DB_URL", System.getenv("DB_URL"));
      String var4 = System.getProperty("DB_USER", System.getenv("DB_USER"));
      String var5 = System.getProperty("DB_PASSWORD", System.getenv("DB_PASSWORD"));
      if (var3 == null || var4 == null || var5 == null) {
         var2.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
               "DB_URL, DB_USER und DB_PASSWORD muessen konfiguriert sein");
         return;
      }
      ArrayList var6 = new ArrayList();

      try {
         System.out.println("UmsatzServlet: Lade DB2-Treiber...");
         Class.forName("com.ibm.db2.jcc.DB2Driver");
         System.out.println("UmsatzServlet: DB2-Treiber geladen!");
         System.out.println("UmsatzServlet: Starte DB2-Abfrage...");
         Connection var7 = DriverManager.getConnection(var3, var4, var5);
         System.out.println("UmsatzServlet: Verbindung erfolgreich!");
         System.out.println("UmsatzServlet: SQL = " + sql);
         Statement var9 = var7.createStatement();
         ResultSet var10 = var9.executeQuery(sql);
         ResultSetMetaData var11 = var10.getMetaData();
         int var12 = var11.getColumnCount();
         System.out.println("UmsatzServlet: Spalten = " + var12);
         int var13 = 0;

         while(var10.next()) {
            ++var13;
            System.out.println("UmsatzServlet: Zeile " + var13 + " gefunden");
            LinkedHashMap var14 = new LinkedHashMap();

            for(int var15 = 1; var15 <= var12; ++var15) {
               String var16 = var11.getColumnName(var15).replace(" ", "_").replace("-", "_").replace("/", "_");
               var14.put(var16, var10.getObject(var15));
            }

            var6.add(var14);
         }

         System.out.println("UmsatzServlet: Gesamtzeilen = " + var13);
         var10.close();
         var9.close();
         var7.close();
      } catch (Exception var17) {
         System.out.println("UmsatzServlet: FEHLER!");
         var17.printStackTrace();
      }

      var2.getWriter().write((new GsonBuilder()).serializeNulls().create().toJson(var6));
   }
}
