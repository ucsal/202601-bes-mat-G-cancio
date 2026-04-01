package br.com.ucsal.olimpiadas.repository;

import java.util.List;

public interface Repository<T> {
    void salvar(T entidade);
    List<T> listarTodos();
}