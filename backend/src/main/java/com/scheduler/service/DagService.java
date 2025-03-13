package com.scheduler.service;

import com.scheduler.model.Dag;

import java.util.List;

public interface DagService {
    List<Dag> getAllDags();
    Dag getDagById(Long id);
    Dag createDag(Dag dag);
    Dag updateDag(Long id, Dag dag);
    void deleteDag(Long id);
    void executeDag(Long id);
    void stopDag(Long id);
}
