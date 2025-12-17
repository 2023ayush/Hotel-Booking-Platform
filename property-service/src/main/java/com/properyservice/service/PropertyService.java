package com.properyservice.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.properyservice.controller.PropertyController;
import com.properyservice.dto.*;
import com.properyservice.entity.*;
import com.properyservice.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;




@Service
public class PropertyService {

    private final PropertyController propertyController;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private EmailProducer emailProducer;

    @Autowired
    private RoomAvailabilityRepository availabilityRepository;


    @Autowired
    private S3Service s3Service;

    PropertyService(PropertyController propertyController) {
        this.propertyController = propertyController;
    }
    @CacheEvict(
            value = {"property", "propertySearch", "room"},
            allEntries = true
    )
    public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {
        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());

        Property property = new Property();
        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
        property.setArea(area);
        property.setCity(city);
        property.setState(state);

        Property savedProperty = propertyRepository.save(property);

        // Save rooms
        for (RoomsDto roomsDto : dto.getRooms()) {
            Rooms rooms = new Rooms();
            rooms.setProperty(savedProperty);
            rooms.setRoomType(roomsDto.getRoomType());
            rooms.setBasePrice(roomsDto.getBasePrice());
            roomRepository.save(rooms);
        }

        // Upload files to S3
        List<String> fileUrls = s3Service.uploadFiles(files);

        // Optionally store file URLs in database or DTO
        dto.setImageUrls(fileUrls); // Ensure PropertyDto has `List<String> imageUrls;`
        emailProducer.sendEmail(new EmailRequest(
                "ayush.backup1997@gmail.com",
                "Property added!",
                "Your property has been successfully added."
        ));
        return dto;
    }
    @Cacheable(
            value = "propertySearch",
            key = "#city + '_' + #date",
            unless = "#result.data == null || #result.data.isEmpty()"
    )
    public APIResponse<List<PropertyDto>> searchProperty(String city, LocalDate date) {

        List<Property> properties = propertyRepository.searchProperty(city, date);

        List<PropertyDto> dtoList = new ArrayList<>();

        for (Property property : properties) {
            PropertyDto dto = new PropertyDto();
            BeanUtils.copyProperties(property, dto);
            dto.setArea(property.getArea().getName());
            dto.setCity(property.getCity().getName());
            dto.setState(property.getState().getName());
            dtoList.add(dto);
        }

        APIResponse<List<PropertyDto>> response = new APIResponse<>();
        response.setStatus(200);
        response.setMessage("Search result");
        response.setData(dtoList);

        return response;
    }

    @Cacheable(value = "property", key = "#id", unless = "#result.data == null")
    public APIResponse<PropertyDto> findPropertyById(long id) {

        APIResponse<PropertyDto> response = new APIResponse<>();

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        PropertyDto dto = new PropertyDto();
        BeanUtils.copyProperties(property, dto);
        dto.setArea(property.getArea().getName());
        dto.setCity(property.getCity().getName());
        dto.setState(property.getState().getName());

        List<RoomsDto> roomsDto = new ArrayList<>();
        for (Rooms room : property.getRooms()) {
            RoomsDto roomDto = new RoomsDto();
            BeanUtils.copyProperties(room, roomDto);
            roomsDto.add(roomDto);
        }
        dto.setRooms(roomsDto);

        response.setStatus(200);
        response.setMessage("Matching Record");
        response.setData(dto);

        return response;
    }


    @Cacheable(value = "availability", key = "#id")
    public List<RoomAvailabilityDto> getTotalRoomsAvailable(long id) {

        List<RoomAvailability> list = availabilityRepository.findByRoomId(id);
        List<RoomAvailabilityDto> dtoList = new ArrayList<>();

        for (RoomAvailability ra : list) {
            RoomAvailabilityDto dto = new RoomAvailabilityDto();
            dto.setDate(ra.getAvailableDate());
            dto.setAvailableRooms(ra.getAvailableCount());
            dtoList.add(dto);
        }

        return dtoList;
    }


    @Cacheable(value = "room", key = "#id")
    public RoomsDto getRoomById(long id) {

        Rooms room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        RoomsDto dto = new RoomsDto();
        BeanUtils.copyProperties(room, dto);

        return dto;
    }




}
