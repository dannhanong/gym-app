package com.hotrodoan.controller;

import com.hotrodoan.dto.response.ResponseMessage;
import com.hotrodoan.model.Position;
import com.hotrodoan.service.PositionService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
@CrossOrigin(origins = "*")
public class PositionController {
    @Autowired
    private PositionService positionService;

//    @GetMapping("")
//    public ResponseEntity<List<Position>> getAllPosition() {
//        return new ResponseEntity<>(positionService.getAllPosition(), HttpStatus.OK);
//    }

    @GetMapping("")
    public ResponseEntity<Page<Position>> findPositionsByNameContaining(@RequestParam(defaultValue = "") String name,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "desc") String order) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortBy)));
        Page<Position> positions = positionService.searchPosition(name, pageable);
        return new ResponseEntity<>(positions, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Position> addPosition(@RequestBody Position position) {
        return new ResponseEntity<>(positionService.addPosition(position), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Position> updatePosition(@RequestBody Position position, @PathVariable Long id) {
        return new ResponseEntity<>(positionService.updatePosition(position, id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePosition(@PathVariable("id") Long id) {
        positionService.deletePosition(id);
        return new ResponseEntity<>(new ResponseMessage("deleted"), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Position> getPosition(@PathVariable("id") Long id) {
        return new ResponseEntity<>(positionService.getPosition(id), HttpStatus.OK);
    }
}
