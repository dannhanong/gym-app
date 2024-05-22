package com.hotrodoan.controller;

import com.hotrodoan.dto.request.GymBranch_RoomDTO;
import com.hotrodoan.dto.request.Room_Amount;
import com.hotrodoan.dto.response.ResponseMessage;
import com.hotrodoan.model.GymBranch;
import com.hotrodoan.model.GymBranch_Room;
import com.hotrodoan.model.Room;
import com.hotrodoan.service.GymBranchService;
import com.hotrodoan.service.GymBranch_RoomService;
import com.hotrodoan.service.MemberService;
import com.hotrodoan.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/gym-branches")
@CrossOrigin(origins = "*")
public class GymBranchController {
    @Autowired
    private GymBranchService gymBranchService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private GymBranch_RoomService gymBranchRoomService;
    @Autowired
    private MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Page<GymBranch>> getAllGymBranches(@RequestParam(defaultValue = "") String keyword,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "id") String sortBy,
                                                            @RequestParam(defaultValue = "desc") String order) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortBy)));
        return new ResponseEntity(gymBranchService.getAllGymBranches(keyword, pageable), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<GymBranch_RoomDTO> addGymBranch(@RequestBody GymBranch_RoomDTO gymBranchRoomDTO) {
        GymBranch gymBranch = new GymBranch();
        gymBranch.setName(gymBranchRoomDTO.getBranchGymName());
        gymBranch.setAddress(gymBranchRoomDTO.getAddress());
        gymBranch.setManager(gymBranchRoomDTO.getManager());

        GymBranch newGymBranch = gymBranchService.createGymBranch(gymBranch);

        List<Room_Amount> theSameRoomOnRoomAmounts = new ArrayList<>();

        for (int i=0; i<gymBranchRoomDTO.getRoomAndAmounts().size(); i++) {
            if (gymBranchRoomDTO.getRoomAndAmounts().get(i).getRoom().getId() == gymBranchRoomDTO.getRoomAndAmounts().get(i+1).getRoom().getId()) {
                gymBranchRoomDTO.getRoomAndAmounts().get(i).setAmount(gymBranchRoomDTO.getRoomAndAmounts().get(i).getAmount() + gymBranchRoomDTO.getRoomAndAmounts().get(i+1).getAmount());
                theSameRoomOnRoomAmounts.add(gymBranchRoomDTO.getRoomAndAmounts().get(i));
            }
        }

        for (Room_Amount roomAmount : gymBranchRoomDTO.getRoomAndAmounts()) {
            GymBranch_Room gymBranchRoom = new GymBranch_Room();
            gymBranchRoom.setGymBranch(newGymBranch);
            gymBranchRoom.setRoom(roomAmount.getRoom());
            gymBranchRoom.setAmount(roomAmount.getAmount());
            gymBranchRoomService.createGymBranch_Room(gymBranchRoom);
        }
        return new ResponseEntity<>(gymBranchRoomDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GymBranch_RoomDTO> updateGymBranch(@RequestBody GymBranch_RoomDTO gymBranchRoomDTO, @PathVariable Long id){
        GymBranch gymBranch = gymBranchService.getGymBranchById(id);

        gymBranch.setName(gymBranchRoomDTO.getBranchGymName());
        gymBranch.setAddress(gymBranchRoomDTO.getAddress());
        gymBranch.setManager(gymBranchRoomDTO.getManager());
        GymBranch updateGymBranch = gymBranchService.updateGymBranch(gymBranch, id);

        List<GymBranch_Room> gymBranchRooms = gymBranchRoomService.getGymBranchesByGymBranch(gymBranch);
        List<Room_Amount> oldRoomAmounts = new ArrayList<>();

//        for (GymBranch_Room gymBranchRoom : gymBranchRooms) {
//            if ()
//        }

        for (GymBranch_Room gymBranchRoom : gymBranchRooms) {
            Room_Amount room_amount = new Room_Amount();
            room_amount.setRoom(gymBranchRoom.getRoom());
            room_amount.setAmount(gymBranchRoom.getAmount());
            oldRoomAmounts.add(room_amount);
        }
        List<Room_Amount> newRoomAmounts = gymBranchRoomDTO.getRoomAndAmounts();

        if (oldRoomAmounts.size() == newRoomAmounts.size()){
            for (int i = 0; i < oldRoomAmounts.size(); i++) {
                Room_Amount oldRoomAmount = oldRoomAmounts.get(i);
                Room_Amount newRoomAmount = newRoomAmounts.get(i);
                oldRoomAmount.setRoom(newRoomAmount.getRoom());
                oldRoomAmount.setAmount(newRoomAmount.getAmount());

                gymBranchRooms.get(i).setRoom(newRoomAmount.getRoom());
                gymBranchRooms.get(i).setAmount(newRoomAmount.getAmount());
                gymBranchRoomService.updateGymBranch_Room(gymBranchRooms.get(i), gymBranchRooms.get(i).getId());
            }
        } else if (oldRoomAmounts.size() < newRoomAmounts.size()) {
            for (int i = 0; i < oldRoomAmounts.size(); i++) {
                Room_Amount oldRoomAmount = oldRoomAmounts.get(i);
                Room_Amount newRoomAmount = newRoomAmounts.get(i);
                oldRoomAmount.setRoom(newRoomAmount.getRoom());
                oldRoomAmount.setAmount(newRoomAmount.getAmount());

                gymBranchRooms.get(i).setRoom(newRoomAmount.getRoom());
                gymBranchRooms.get(i).setAmount(newRoomAmount.getAmount());
                gymBranchRoomService.updateGymBranch_Room(gymBranchRooms.get(i), gymBranchRooms.get(i).getId());
            }
            for (int i = oldRoomAmounts.size(); i < newRoomAmounts.size(); i++) {
                Room_Amount newRoomAmount = newRoomAmounts.get(i);
                GymBranch_Room gymBranchRoom = new GymBranch_Room();
                gymBranchRoom.setGymBranch(gymBranch);
                gymBranchRoom.setRoom(newRoomAmount.getRoom());
                gymBranchRoom.setAmount(newRoomAmount.getAmount());
                gymBranchRoomService.createGymBranch_Room(gymBranchRoom);
            }
        }
        else {
            for (int i = 0; i < newRoomAmounts.size(); i++) {
                Room_Amount oldRoomAmount = oldRoomAmounts.get(i);
                Room_Amount newRoomAmount = newRoomAmounts.get(i);
                oldRoomAmount.setRoom(newRoomAmount.getRoom());
                oldRoomAmount.setAmount(newRoomAmount.getAmount());

                gymBranchRooms.get(i).setRoom(newRoomAmount.getRoom());
                gymBranchRooms.get(i).setAmount(newRoomAmount.getAmount());
                gymBranchRoomService.updateGymBranch_Room(gymBranchRooms.get(i), gymBranchRooms.get(i).getId());
            }
            for (int i = newRoomAmounts.size(); i < oldRoomAmounts.size(); i++) {
                gymBranchRoomService.deleteGymBranch_Room(gymBranchRooms.get(i).getId());
            }
        }
        return new ResponseEntity<>(gymBranchRoomDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteGymBranch(@PathVariable("id") Long id) {
        gymBranchService.deleteGymBranchById(id);
        return new ResponseEntity<>(new ResponseMessage("deleted"), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymBranch> getGymBranch(@PathVariable("id") Long id) {
        return new ResponseEntity<>(gymBranchService.getGymBranchById(id), HttpStatus.OK);
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<GymBranch_RoomDTO> getGymBranchRoom(@PathVariable("id") Long id) {
        GymBranch gymBranch = gymBranchService.getGymBranchById(id);
        GymBranch_RoomDTO gymBranch_RoomDTO = new GymBranch_RoomDTO();
        gymBranch_RoomDTO.setBranchGymName(gymBranch.getName());
        gymBranch_RoomDTO.setAddress(gymBranch.getAddress());
        gymBranch_RoomDTO.setManager(gymBranch.getManager());

        List<GymBranch_Room> gymBranchRooms = gymBranchRoomService.getGymBranchesByGymBranch(gymBranch);
        List<Room_Amount> roomAmounts = new ArrayList<>();
        for (GymBranch_Room gymBranchRoom : gymBranchRooms) {
            Room_Amount room_amount = new Room_Amount();
            room_amount.setRoom(gymBranchRoom.getRoom());
            room_amount.setAmount(gymBranchRoom.getAmount());
            roomAmounts.add(room_amount);
        }

//        int totalMember = memberService.countByGymBranch(gymBranch);

        gymBranch_RoomDTO.setRoomAndAmounts(roomAmounts);

        return new ResponseEntity<>(gymBranch_RoomDTO, HttpStatus.OK);
    }
}
