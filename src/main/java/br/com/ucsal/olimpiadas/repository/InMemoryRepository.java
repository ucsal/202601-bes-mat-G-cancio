package br.com.ucsal.olimpiadas.repository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository<T> implements Repository<T> {

    private final List<T> entidades = new ArrayList<>();

    @Override
    public void salvar(T entidade) {
        entidades.add(entidade);
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(entidades); // cópia defensiva
    }
}