package Controller;

import controller.GestioneCampagnaController;
import model.beans.Campagna;
import model.beans.Categoria;
import model.beans.StatoCampagna;
import model.beans.Utente;
import model.services.CampagnaService;
import model.services.CategoriaService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;


import static org.mockito.Mockito.*;

public class CampagnaControllerTest {
    Campagna campagna;
    Utente utente;
    GestioneCampagnaController campagnaController;
    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    HttpSession mockSession;
    ServletContext mockContext;
    CampagnaService mockService;
    CategoriaService mockCateogriaService;
    RequestDispatcher mockDispatcher;
    final String CONTEXT_PATH = "/FundIt-1.0-SNAPSHOT";


    @Before
    public void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockSession = Mockito.mock(HttpSession.class);
        mockContext = Mockito.mock(ServletContext.class);
        mockDispatcher = Mockito.mock(RequestDispatcher.class);
        mockService = Mockito.mock(CampagnaService.class);
        mockCateogriaService = Mockito.mock(CategoriaService.class);
        campagnaController = new GestioneCampagnaController(mockService, mockCateogriaService);
        campagna = Mockito.mock(Campagna.class);
        utente = Mockito.mock(Utente.class);
    }

    @Test
    public void testGetNotValidCreazioneCampagna() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/creaCampagna");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockSession.getAttribute("utente"))
                .thenReturn(null);


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getSession();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockResponse, atLeastOnce()).sendRedirect(anyString());
    }

    @Test
    public void testGetValidCreazioneCampagna() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/creaCampagna");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(utente);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).setAttribute(anyString(), anyList());
        verify(mockDispatcher, atLeastOnce()).forward(mockRequest, mockResponse);
    }

    @Test
    public void testCasePathNotFound() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("ciao");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(utente);

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce())
                .getPathInfo();
        verify(mockResponse, atLeastOnce())
                .sendError(anyInt(), anyString());
    }

    @Test
    public void testGetMain() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/main");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(utente);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce())
                .getPathInfo();
        verify(mockRequest, atLeastOnce())
                .getSession();
        verify(mockSession, atLeastOnce())
                .getAttribute("utente");
        verify(mockDispatcher, atLeastOnce())
                .forward(mockRequest, mockResponse);
    }

    @Test
    public void testGetNotValidModificaCampagna() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/modificaCampagna");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(null);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getPathInfo();
        verify(mockRequest, atLeastOnce()).getSession();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockResponse, atLeastOnce()).sendRedirect(anyString());
    }

    @Test
    public void testGetValidModificaCampagna() throws ServletException, IOException {
        Utente utente1 = Mockito.mock(Utente.class);
        when(mockRequest.getPathInfo())
                .thenReturn("/modificaCampagna");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(utente);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockRequest.getParameter("idCampagna"))
                .thenReturn("2");
        when(mockService.trovaCampagna(anyInt()))
                .thenReturn(campagna);
        when(campagna.getUtente())
                .thenReturn(utente1);
        when(utente1.getIdUtente())
                .thenReturn(1);
        when(utente.getIdUtente())
                .thenReturn(2);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getSession();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).getParameter("idCampagna");
        verify(mockService, atLeastOnce()).trovaCampagna(anyInt());
        verify(mockResponse, atLeastOnce()).sendError(anyInt(), anyString());
    }

    @Test
    public void testGetModificaCategoriaIds() throws ServletException, IOException {
        Utente utente1 = Mockito.mock(Utente.class);
        when(mockRequest.getPathInfo())
                .thenReturn("/modificaCampagna");
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("utente"))
                .thenReturn(utente);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockRequest.getParameter("idCampagna"))
                .thenReturn("2");
        when(mockService.trovaCampagna(anyInt()))
                .thenReturn(campagna);
        when(campagna.getUtente())
                .thenReturn(utente1);
        when(utente1.getIdUtente())
                .thenReturn(1);
        when(utente.getIdUtente())
                .thenReturn(1);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getSession();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).getParameter("idCampagna");
        verify(mockService, atLeastOnce()).trovaCampagna(anyInt());
        verify(mockRequest, atLeastOnce()).setAttribute(anyString(), any(Campagna.class));
        verify(mockDispatcher, atLeastOnce()).forward(mockRequest, mockResponse);

    }

    @Test
    public void testGetPathInfoNull() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn(null);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("utente")).thenReturn(utente);

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getPathInfo();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
    }

    @Test
    public void testGetCampagnaNull() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/campagna");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getParameter("idCampagna"))
                .thenReturn("2");
        when(mockService.trovaCampagna(anyInt()))
                .thenReturn(null);

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getPathInfo();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).getParameter("idCampagna");
        verify(mockResponse, atLeastOnce()).sendError(anyInt(), anyString());
    }

    @Test
    public void testGetCampagna() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/campagna");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getParameter("idCampagna"))
                .thenReturn("2");
        when(mockService.trovaCampagna(anyInt()))
                .thenReturn(campagna);
        when(campagna.getStato())
                .thenReturn(StatoCampagna.ATTIVA);
        when(campagna.getUtente())
                .thenReturn(utente);
        when(utente.getIdUtente())
                .thenReturn(2);
        when(utente.getCf())
                .thenReturn("carmine fierro");

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getPathInfo();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).getParameter("idCampagna");
        verify(mockResponse, atLeastOnce()).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testGetCampagnaUpdated() throws ServletException, IOException {
        when(mockRequest.getPathInfo())
                .thenReturn("/campagna");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getParameter("idCampagna"))
                .thenReturn("2");
        when(mockService.trovaCampagna(anyInt()))
                .thenReturn(campagna);
        when(campagna.getStato())
                .thenReturn(StatoCampagna.ATTIVA);
        when(campagna.getUtente())
                .thenReturn(utente);
        when(utente.getIdUtente())
                .thenReturn(2);
        when(utente.getCf())
                .thenReturn("carmine fierro");
        when(mockService.modificaCampagna(any(Campagna.class)))
                .thenReturn(true);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce()).getPathInfo();
        verify(mockSession, atLeastOnce()).getAttribute("utente");
        verify(mockRequest, atLeastOnce()).getParameter("idCampagna");
        verify(mockRequest, atLeastOnce()).setAttribute("campagna", campagna);
        verify(mockDispatcher, atLeastOnce()).forward(mockRequest, mockResponse);
    }

    @Test
    public void testGetRicercaCampagna() throws ServletException, IOException {
        Campagna campagna1 = new Campagna();
        Campagna campagna2 = new Campagna();
        campagna1.setIdCampagna(1);
        campagna1.setStato(StatoCampagna.ATTIVA);
        campagna2.setIdCampagna(2);

        when(mockRequest.getPathInfo())
                .thenReturn("/ricerca");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getParameter("searchText"))
                .thenReturn("testo");
        when(mockService.ricercaCampagna(anyString()))
                .thenReturn(List.of(campagna1, campagna1, campagna2));


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce())
                .setAttribute(anyString(), anyList());

        verify(mockDispatcher, atLeastOnce())
                .forward(mockRequest, mockResponse);
    }

    @Test
    public void testGetRicercaCampagneNotFound() throws ServletException, IOException {
        Campagna campagna1 = new Campagna();
        Campagna campagna2 = new Campagna();
        campagna1.setIdCampagna(1);
        campagna1.setStato(StatoCampagna.CHIUSA);
        campagna2.setIdCampagna(2);

        when(mockRequest.getPathInfo())
                .thenReturn("/ricerca");
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockContext.getContextPath())
                .thenReturn(CONTEXT_PATH);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getParameter("searchText"))
                .thenReturn("testo");
        when(mockService.ricercaCampagna(anyString()))
                .thenReturn(List.of(campagna1, campagna1, campagna2));


        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce())
                .setAttribute(anyString(), anyString());

        verify(mockDispatcher, atLeastOnce())
                .forward(mockRequest, mockResponse);
    }

    @Test
    public void testGetRicercaPerCategoria() throws ServletException, IOException {
        Categoria categoria = Mockito.mock(Categoria.class);
        Campagna campagna1 = new Campagna();
        Campagna campagna2 = new Campagna();

        campagna1.setIdCampagna(4);
        campagna1.setStato(StatoCampagna.ATTIVA);
        campagna2.setIdCampagna(5);
        campagna2.setStato(StatoCampagna.CHIUSA);

        when(mockRequest.getPathInfo())
                .thenReturn("/ricercaCategoria");
        when(mockRequest.getServletContext())
                .thenReturn(mockContext);
        when(mockRequest.getSession())
                .thenReturn(mockSession);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mockDispatcher);
        when(mockRequest.getParameter("idCat"))
                .thenReturn("3");
        when(mockCateogriaService.visualizzaCategoria(categoria))
                .thenReturn(categoria);
        when(mockService.ricercaCampagnaPerCategoria(anyString()))
                .thenReturn(List.of(campagna1, campagna1, campagna2));

        campagnaController.doGet(mockRequest, mockResponse);

        verify(mockRequest, atLeastOnce())
                .setAttribute(anyString(), anyList());
        verify(mockDispatcher, atLeastOnce()).forward(mockRequest, mockResponse);
    }
}
