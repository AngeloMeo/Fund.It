package controller;


import controller.utils.Validator;
import model.DAO.CampagnaDAO;
import model.DAO.SegnalazioneDAO;
import model.DAO.UtenteDAO;
import model.beans.Campagna;
import model.beans.Segnalazione;
import model.beans.StatoSegnalazione;
import model.beans.Utente;
import model.beans.proxies.CampagnaProxy;
import model.beans.proxyInterfaces.CampagnaInterface;
import model.services.CampagnaService;
import model.services.CampagnaServiceImpl;
import model.services.SegnalazioniService;
import model.services.SegnalazioniServiceImpl;
import model.services.UtenteService;
import model.services.UtenteServiceImpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@WebServlet(name = "SegnalazioneController",
        value = "/segnalazioni/*")
public final class SegnalazioneController extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        HttpSession session = request.getSession();
        String idSegnalazione = request.getParameter("idSegnalazione");
        int id = 0;
        try {
            id = Integer.parseInt(idSegnalazione);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        SegnalazioniService service = new SegnalazioniServiceImpl(new SegnalazioneDAO());


        switch (path) {
            case "/get":
                Segnalazione s = service.trovaSegnalazione(id);
                request.setAttribute("segnalazione", s);
                break;
            case "/getAll":
                List<Segnalazione> segnalazioni = service.trovaSegnalazioni();
                request.setAttribute("segnalazioni", segnalazioni);
                break;
            default:
                break;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("");
        dispatcher.forward(request, response);

    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws IOException {

        String path = request.getPathInfo();
        HttpSession session = request.getSession();

        Validator val = new Validator(request);
        if (val.isValidBean(new Utente(), session.getAttribute("utente"))) {
            response.sendRedirect(
                    getServletContext().getContextPath() + "/index.jsp");
            return;
        }
        Utente userSession = (Utente) session.getAttribute("utente");
        String idCampagna = request.getParameter("idCampagna");
        CampagnaService campagnaService =
                new CampagnaServiceImpl(new CampagnaDAO());
        SegnalazioniService segnalazioniService =
                new SegnalazioniServiceImpl(new SegnalazioneDAO());
        UtenteService utenteService = new UtenteServiceImpl(new UtenteDAO());

        String descrizione = request.getParameter("descrizione");
        String resource = "/";
        Segnalazione segnalazione = new Segnalazione();

        switch (path) {
            case "/segnala":
                Campagna c = campagnaService.
                        trovaCampagna(Integer.parseInt(idCampagna));
                Utente utente = new Utente();
                utente.setIdUtente(//todo attenzione al campo; la jsp manda solo idutente
                        Integer.parseInt(request.getParameter("idUtente")));
                segnalazione.setStatoSegnalazione(StatoSegnalazione.ATTIVA);
                segnalazione.setSegnalatore(utente);
                segnalazione.setCampagnaSegnalata(c);
                segnalazione.setSegnalato(c.getUtente());
                segnalazione.setDescrizione(descrizione);
                segnalazione.setDataOra(LocalDateTime.now());
                if (segnalazioniService.
                        segnalaCampagna(segnalazione)) {
                    response.sendRedirect(
                            getServletContext().getContextPath()
                                    + "/index.jsp");
                    return;
                }
                break;
            case "/risolvi":
                if (!userSession.isAdmin()) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Non autorizzato");
                    return;
                }
                String scelta = request.getParameter("sceltaSegnalazione");
                int id = Integer.parseInt(request.getParameter("idCampagna"));
                int idSegnalazione =
                        Integer.parseInt(
                                request.getParameter("idSegnalazione"));

                Campagna campagna = campagnaService.trovaCampagna(id);
                if (scelta.equals("Risolvi")) {
                    segnalazioniService
                            .risolviSegnalazione(idSegnalazione,
                                    StatoSegnalazione.RISOLTA);
                    Utente utenteSegnalato =
                            utenteService.visualizzaDashboardUtente(
                                    campagna.getUtente().getIdUtente());
                    campagnaService.cancellaCampagna(campagna);
                    utenteService.sospensioneUtente(utenteSegnalato);
                    CampagnaInterface campagnaProxy =
                            new CampagnaProxy(campagna);
                    if (campagnaService
                            .rimborsaDonazioni(campagna, campagnaProxy)) {
                        System.out.println("tutto bene");
                    } else {
                        System.out.println("errore");
                    }
                } else {
                    segnalazioniService
                            .risolviSegnalazione(idSegnalazione,
                                    StatoSegnalazione.ARCHIVIATA);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
