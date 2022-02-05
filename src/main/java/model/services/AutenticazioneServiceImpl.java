package model.services;

import model.DAO.DAO;
import model.DAO.UtenteDAO;
import model.beans.Utente;


import javax.servlet.http.HttpSession;

public class AutenticazioneServiceImpl implements AutenticazioneService {

    /**
     * Wrapper di HttpSession.
     */
    private final HttpSession sessionWrapper;
    /**
     * Wrapper di UtenteDAO.
     */
    private final DAO<Utente> dao;

    /**
     * @param session   Sessione attuale dell'utente
     * @param utenteDAO istanza di UtenteDAO
     */
    public AutenticazioneServiceImpl(final HttpSession session,
                                     final DAO<Utente> utenteDAO) {
        this.sessionWrapper = session;
        this.dao = utenteDAO;
    }

    /**
     * @param utente Istanza di Utente che desidera fare il login
     * @return un'instanza della classe utente
     */
    @Override
    public Utente login(final Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("Argument must be not null");
        } else {
            UtenteDAO userDao = (UtenteDAO) dao;
            Utente ut = userDao.doLogin(utente);
            if (ut == null) {
                return null;
            } else {
                if (ut.getDataBan() != null) {
                    ut.setIdUtente(-1);
                    return ut;
                } else {
                    return ut;
                }
            }
        }
    }

    /**
     * @param utente Istanza di Utente che desidera fare la registrazione
     * @return true se l'operazione va a buon fine, false altrimenti
     */
    @Override
    public boolean registrazione(final Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("Argument must be not null");
        } else {
            return dao.save(utente);
        }
    }

    /**
     * Esegue il logout dell'utente presente in sessione.
     */
    @Override
    public void logout() {
        sessionWrapper.invalidate();
    }
}
