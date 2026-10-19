package org.example.sistema_bancario_po.dao;

public class DaoException extends Exception {
    public DaoException(String mensagem) {
        super(mensagem);
    }

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}