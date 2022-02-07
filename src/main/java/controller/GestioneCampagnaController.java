package controller;

import controller.utils.FileServlet;
import controller.utils.Validator;
import model.DAO.CampagnaDAO;
import model.DAO.CategoriaDAO;
import model.DAO.ImmagineDAO;
import model.DAO.DAO;
import model.beans.Campagna;
import model.beans.Categoria;
import model.beans.Donazione;
import model.beans.Immagine;
import model.beans.StatoCampagna;
import model.beans.Utente;
import model.beans.proxies.CampagnaProxy;
import model.beans.proxies.DonazioneProxy;
import model.beans.proxyInterfaces.CampagnaInterface;
import model.services.CampagnaService;
import model.services.CampagnaServiceImpl;
import model.services.CategoriaService;
import model.services.CategoriaServiceImpl;
import model.services.ImmagineService;
import model.services.ImmagineServiceImpl;
import model.storage.ConPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "GestioneCampagnaController",
        value = "/campagna/*",
        loadOnStartup = 0)
@MultipartConfig
public final class GestioneCampagnaController extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        List<Campagna> campagne;
        DAO<Campagna> campagnaDAO = new CampagnaDAO();
        campagne = campagnaDAO.getAll();
        campagne = campagne.stream().
                filter(campagna -> campagna.getStato()
                        == StatoCampagna.ATTIVA).
                collect(Collectors.toList());
        campagne.forEach(c -> {
            CampagnaInterface proxy = new CampagnaProxy(c);
            proxy.getUtente();
            proxy.getImmagini();
        });
        getServletContext().setAttribute("campagneList", campagne);
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        String resource = "/";
        HttpSession session = request.getSession();
        CampagnaService service = new CampagnaServiceImpl();
        CategoriaService categoriaService =
                new CategoriaServiceImpl(new CategoriaDAO());


        switch (request.getPathInfo()) {
            case "/main":
                CampagnaService cs = new CampagnaServiceImpl();
                List<Campagna> lst = cs.getActiveCampagne();
                lst.forEach(c -> {
                    CampagnaInterface proxy = new CampagnaProxy(c);
                    proxy.getUtente();
                    proxy.getImmagini();
                });
                request.setAttribute("campagneList", lst);
                resource = "/WEB-INF/results/main_page.jsp";
                break;
            case "/creaCampagna":
                if (!new Validator(request).isValidBean(Utente.class,
                        session.getAttribute("utente"))) {
                    response.sendRedirect(
                            getServletContext().getContextPath()
                                    + "/autenticazione/login");
                    return;
                } else {
                    request.setAttribute("categorie",
                            categoriaService.visualizzaCategorie());
                    resource = "/WEB-INF/results/form_campagna.jsp";
                }
                break;
            case "/modificaCampagna":
                visualizzaModificaCampagna(request, response);
                return;
            case "/campagna":
                String id = request.getParameter("idCampagna");
                int idCampagna = Integer.parseInt(id);
                Campagna c = service.trovaCampagna(idCampagna);
                if (c == null || c.getStato() != StatoCampagna.ATTIVA) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Campagna non trovata");
                    return;
                } else {
                    CampagnaInterface proxy = new CampagnaProxy(c);
                    c.setUtente(proxy.getUtente());
                    DonazioneProxy proxy2 = new DonazioneProxy();
                    c.setImmagini(proxy.getImmagini());
                    List<Donazione> donazioni = proxy.getDonazioni();
                    donazioni.forEach(d -> {
                        proxy2.setDonazione(d);
                        d.setUtente(proxy2.getUtente());
                    });
                    c.setDonazioni(proxy.getDonazioni());
                    if (service.modificaCampagna(c)) {
                        request.setAttribute("campagna", c);
                        condividiCampagna(request, response, c.getIdCampagna());
                        resource = "/WEB-INF/results/campagna.jsp";
                    } else {
                        System.out.println("mammt");
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
                break;
            case "/ricerca":
                String searchText = request.getParameter("searchText");
                searchText = searchText.trim();
                List<Campagna> campagne = service.ricercaCampagna(searchText);
                campagne = campagne.stream().
                        filter(campagna -> campagna.getStato()
                                == StatoCampagna.ATTIVA).
                        collect(Collectors.toList());

                if (campagne.size() > 0 && !searchText.isBlank()) {
                    for (Campagna campagna : campagne) {
                        new CampagnaProxy(campagna).getImmagini();
                    }
                    request.setAttribute("campagneList", campagne);
                    resource = "/WEB-INF/results/campagne.jsp";
                } else {
                    request.setAttribute("errorSearch",
                            "Nessun risultato trovato");
                    resource = "/WEB-INF/results/campagne.jsp";
                }
                break;
            default:
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Risorsa non trovata");
                return;
        }

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(resource);
        dispatcher.forward(request, response);
    }

    private void condividiCampagna(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   final int idCampagna) throws IOException {
        CampagnaService cs = new CampagnaServiceImpl();
        Map<String, String> map =
                cs.condividiCampagna(idCampagna, request);

        if (map != null) {
            request.setAttribute("linkList", map);
        } else {
            response.sendRedirect(
                    getServletContext().getContextPath()
                            + "/index.jsp");
        }
        return;
    }

    private void visualizzaModificaCampagna(final HttpServletRequest request,
                                            final HttpServletResponse response)
            throws ServletException, IOException {
        CampagnaService service = new CampagnaServiceImpl();
        CategoriaService categoriaService =
                new CategoriaServiceImpl(new CategoriaDAO());
        HttpSession session = request.getSession();

        if (!new Validator(request).isValidBean(Utente.class,
                session.getAttribute("utente") == null)) {
            response.sendRedirect(
                    getServletContext().getContextPath()
                            + "/autenticazione/login");
            return;
        }

        Utente ut = (Utente) request.getSession().getAttribute("utente");
        String idCampagna = request.getParameter("idCampagna");
        int id;
        try {
            id = Integer.parseInt(idCampagna);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "Input errato");
            e.printStackTrace();
            return;
        }
        Campagna c = service.trovaCampagna(id);
        if (c.getUtente().getIdUtente() != ut.getIdUtente()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Non Autorizzato");
            return;
        }
        request.setAttribute("campagna", c);
        request.setAttribute("categorie",
                categoriaService.visualizzaCategorie());
        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        "/WEB-INF/results/form_campagna.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        CampagnaService service = new CampagnaServiceImpl();

        if (!new Validator(request).isValidBean(Utente.class,
                session.getAttribute("utente"))) {
            response.sendRedirect(
                    getServletContext().getContextPath()
                            + "/autenticazione/login");
            System.out.println("we");
            return;
        }

        Utente userSession = (Utente) session.getAttribute("utente");
        String idCampagna = request.getParameter("idCampagna");
        int id = 0;
        String path = request.getPathInfo()
                == null ? "/" : request.getPathInfo();

        switch (path) {
            case "/creaCampagna":
                creaCampagna(request, response, userSession);
                break;
            case "/modificaCampagna":
                if (idCampagna != null) {
                    modificaCampagna(request, response, service
                            .trovaCampagna(Integer.parseInt(idCampagna)),
                            userSession);
                }
                break;
            case "/cancellaCampagna":
                id = Integer.parseInt(idCampagna);
                Campagna campagna = service.trovaCampagna(id);

                    if (campagna.getUtente().getIdUtente()
                            != userSession.getIdUtente()) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "Non Autorizzato");
                        return;
                    }


                if (service.cancellaCampagna(campagna)) {
                    if (service.rimborsaDonazioni(campagna,
                            new CampagnaProxy(campagna))) {
                        System.out.println("rimborso ok");
                    } else {
                        System.out.println("rimborso errore");
                    }
                    System.out.println("cancellazione Ok");
                } else {
                    System.out.println("cancellazione errore");
                }
                break;
            case "/chiudiCampagna":
                id = Integer.parseInt(idCampagna);
                Campagna campagna1 = service.trovaCampagna(id);
                if (service.chiudiCampagna(campagna1)) {
                    response.sendRedirect(getServletContext().getContextPath()
                            + "/GestioneUtenteController/visualizzaDashboard");
                } else {
                    response.sendError(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                return;
            default:
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Risorsa non trovata");
                break;
        }
    }

    @Override
    public void destroy() {
        ConPool.getInstance().closeDataSource();
        super.destroy();
    }

    private void creaCampagna(final HttpServletRequest req,
                              final HttpServletResponse res,
                              final Utente utente)
            throws IOException, ServletException {

        Campagna c = extractCampagna(req);
        c.setSommaRaccolta(0d);
        c.setUtente(utente);
        CampagnaService cs = new CampagnaServiceImpl();
        if (cs.creazioneCampagna(c)) {
            uploadFoto(req, c);
            res.sendRedirect(
                    getServletContext().getContextPath() + "/index.jsp");
        } else {
            res.sendRedirect(
                    getServletContext().getContextPath()
                            + "/campagna/creaCampagna");
        }
    }

    private Campagna extractCampagna(final HttpServletRequest request) {
        Campagna c = new Campagna();

        c.setStato(StatoCampagna.ATTIVA);
        c.setTitolo(request.getParameter("titolo"));
        c.setDescrizione(request.getParameter("descrizione"));

        c.setSommaTarget(
                Double.parseDouble(request.getParameter("sommaTarget")));
        c.setUtente((Utente) request.getSession(false).getAttribute("utente"));

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(Integer.parseInt(
                request.getParameter("idCategoria")));

        CategoriaService cs = new CategoriaServiceImpl();

        c.setCategoria(
                cs.visualizzaCategoria(categoria));

        return c;
    }

    private void uploadFoto(final HttpServletRequest request,
                            final Campagna campagna) throws ServletException,
            IOException {
        List<String> fotoList = FileServlet.uploadFoto(request);
        ImmagineService immagineService =
                new ImmagineServiceImpl(new ImmagineDAO());
        Immagine immagine = new Immagine();
        immagine.setCampagna(campagna);

        if (!fotoList.isEmpty()) {
            immagineService.eliminaImmaginiCampagna(campagna.getIdCampagna());
        }

        for (String fotoPath : fotoList) {
            immagine.setPath(fotoPath);
            immagineService.salvaImmagine(immagine);
        }
    }

    private void modificaCampagna(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final Campagna campagna,
                                  final Utente utente)
            throws IOException, ServletException {

        Campagna c = extractCampagna(request);

        if (c.getUtente().getIdUtente() != utente.getIdUtente()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Non Autorizzato");
            return;
        }

        c.setIdCampagna(campagna.getIdCampagna());
        c.setSommaRaccolta(campagna.getSommaRaccolta());
        CampagnaService cs = new CampagnaServiceImpl();

        if (cs.modificaCampagna(c)) {
            uploadFoto(request, c);
            response.sendRedirect(
                    getServletContext().getContextPath() + "/index.jsp");
        } else {
            request.getRequestDispatcher("/campagna"
                    + "/modificaCampagna").forward(request, response);
        }
    }
}
