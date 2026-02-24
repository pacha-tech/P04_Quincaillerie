package com.ict300.P04.repository.interfacesImpl.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuincaillerieInterfaceImpl implements QuincaillerieCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> findNameOnly() {
        String jpql = "SELECT DISTINCT q.storeName " +
                "FROM Quincaillerie q " ;

        return entityManager.createQuery(jpql , String.class)
                .getResultList();
    }

    @Override
    public Quincaillerie getQuincaillerie(String idQuincaillerie) {
        String jpql = "SELECT q " +
                "FROM Quincaillerie q " +
                "WHERE q.idQuincaillerie = :id ";

        return entityManager.createQuery(jpql , Quincaillerie.class)
                .setParameter("id" , idQuincaillerie)
                .getSingleResult();
    }
}
