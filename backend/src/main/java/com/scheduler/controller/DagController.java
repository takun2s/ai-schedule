package com.scheduler.controller;

import com.scheduler.common.Result;
import com.scheduler.model.Dag;
import com.scheduler.service.DagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dags")
public class DagController {

    @Autowired
    private DagService dagService;

    @GetMapping
    public Result<List<Dag>> listDags() {
        return Result.success(dagService.getAllDags());
    }

    @GetMapping("/{id}")
    public Result<Dag> getDag(@PathVariable Long id) {
        return Result.success(dagService.getDagById(id));
    }

    @PostMapping
    public Result<Dag> createDag(@RequestBody Dag dag) {
        return Result.success(dagService.createDag(dag));
    }

    @PutMapping("/{id}")
    public Result<Dag> updateDag(@PathVariable Long id, @RequestBody Dag dag) {
        return Result.success(dagService.updateDag(id, dag));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDag(@PathVariable Long id) {
        dagService.deleteDag(id);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<?> executeDag(@PathVariable Long id) {
        try {
            dagService.executeDag(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "执行DAG失败: " + e.getMessage());
        }
    }
}
