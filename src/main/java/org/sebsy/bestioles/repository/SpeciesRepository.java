package org.sebsy.bestioles.repository;

import java.util.List;
import java.util.Optional;

import org.sebsy.bestioles.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpeciesRepository extends JpaRepository<Species, Integer> {

    Optional<Species> findFirstByCommonName(String commonName);

    List<Species> findByLatinNameContainingIgnoreCase(String fragment);

    @Query("select s from Species s order by s.commonName")
    List<Species> toutesTrieesParNomCommun();

    @Query("select s from Species s where s.commonName like :motif")
    List<Species> nomCommunCommeMotif(@Param("motif") String motif);
}