package model.DAO;

import model.beans.Campagna;
import model.beans.Segnalazione;
import model.beans.StatoSegnalazione;
import model.beans.Utente;
import model.storage.ConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SegnalazioneDAO
        implements DAOHelper<Segnalazione> {
    /**
     * @param id rappresenta l'identificativo dell'entity
     * @return null se non viene trovato nessun risultato,
     * un'istanza di T nel caso in cui viene trovato un risultato
     */
    @Override
    public Segnalazione getById(final int id) {
        try (Connection connection = ConPool.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             "SELECT * "
                                     + "FROM segnalazione AS s"
                                     + " WHERE s.idSegnalazione = ?")) {
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return extract(set, "s");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * @return una lista di entity T
     */
    @Override
    public List<Segnalazione> getAll() {
        String sql = "SELECT * FROM segnalazione";
        List<Segnalazione> list;
        try (Connection connection = ConPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            list = new ArrayList<>();
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                list.add(extract(set, "s"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @param entity l'istanza da salvare
     * @return false --> se l'operazione non va a buon fine,
     * true --> se l'operazione va a buon fine
     */
    @Override
    public boolean save(final Segnalazione entity) {
        try (Connection connection = ConPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO segnalazione "
                             + "(DataOra, descrizione,"
                             + " idUtenteSegnalatore,"
                             + " idUtenteSegnalato, Stato, idCampagna)"
                             + "values (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            int ret1 = fillPreparedStatement(statement, entity);
            int ret = statement.executeUpdate();
            ResultSet set = statement.getGeneratedKeys();
            if (set.next()) {
                entity.setIdSegnalazione(set.getInt(0));
            }
            return (ret > 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param entity l'istanza da aggiornare
     * @return false --> se l'operazione non va a buon fine,
     * true --> se l'operazione va a buon fine
     */
    @Override
    public boolean update(final Segnalazione entity) {
        int ret;
        try (Connection connection = ConPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE segnalazione "
                             + "SET Stato = ? WHERE idSegnalazione = ?")) {

            statement.setString(1, entity.getStatoSegnalazione().toString());
            statement.setInt(2, entity.getIdSegnalazione());

            ret = statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret > 0;
    }

    /**
     * @param entity l'istanza da eliminare
     * @return false --> se l'operazione non va a buon fine,
     * true --> se l'operazione va a buon fine
     */
    @Override
    public boolean delete(final Segnalazione entity) {
        int ret;
        try (Connection connection = ConPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM segnalazione "
                             + " WHERE idSegnalazione = ?")) {
            statement.setInt(1, entity.getIdSegnalazione());


            ret = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret > 0;
    }

    /**
     * @param resultSet resultSet della query eseguita
     * @param alias     eventuale alias del field
     * @return l'istanza della della classe T popolata con le informazioni
     * presenti nel resultSet
     * @throws SQLException eccezione lanciata in caso di problemi
     */
    @Override
    public Segnalazione extract(final ResultSet resultSet, final String alias)
            throws SQLException {
        Segnalazione s = new Segnalazione();
        s.setIdSegnalazione(resultSet.getInt(alias + ".idSegnalazione"));
        s.setDataOra(resultSet.getDate(alias + ".DataOra"));
        s.setStatoSegnalazione(StatoSegnalazione.valueOf(
                resultSet.getString(alias + ".Stato")));
        Utente segnalato = new Utente();
        segnalato.setIdUtente(resultSet.getInt(alias + ".idUtenteSegnalato"));
        s.setSegnalato(segnalato);
        Utente segnalatore = new Utente();
        segnalatore.setIdUtente(
                resultSet.getInt(alias + ".idUtenteSegnalatore"));
        s.setSegnalatore(segnalatore);
        s.setDescrizione(resultSet.getString(alias + ".descrizione"));
        Campagna c = new Campagna();
        c.setIdCampagna(resultSet.getInt(alias + ".idCampagna"));
        s.setCampagnaSegnalata(c);
        return s;
    }

    /**
     * @param preparedStatement il prepared Statement da popolare
     * @param entity            l'entity sorgente dei dati
     * @return l'index del campo successivo da riempire
     * @throws SQLException eccezione lanciata in caso di problemi
     */
    @Override
    public int fillPreparedStatement(final PreparedStatement preparedStatement,
                                     final Segnalazione entity)
            throws SQLException {
        int index = 1;
        preparedStatement.setDate(index++, (java.sql.Date) entity.getDataOra());
        preparedStatement.setString(index++, entity.getDescrizione());
        preparedStatement.setInt(index++,
                entity.getSegnalatore().getIdUtente());
        preparedStatement.setInt(index++, entity.getSegnalato().getIdUtente());
        preparedStatement.setString(index++,
                entity.getStatoSegnalazione().toString());
        preparedStatement.setInt(index++,
                entity.getCampagnaSegnalata().getIdCampagna());
        return index;
    }
}