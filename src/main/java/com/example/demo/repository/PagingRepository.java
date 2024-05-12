package com.example.demo.repository;
import com.example.demo.domain.Entity;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID,E>
{
    Page<E> findAllPAGINAT(Pageable pageable);
}
