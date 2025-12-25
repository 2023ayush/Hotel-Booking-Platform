package com.properyservice.service;

import com.properyservice.dto.*;
import com.properyservice.entity.*;
import com.properyservice.exception.InvalidRequestException;
import com.properyservice.exception.ResourceNotFoundException;
import com.properyservice.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AreaRepository areaRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository availabilityRepository;
    private final EmailProducer emailProducer;
    private final S3Service s3Service;

    public PropertyService(PropertyRepository propertyRepository,
                           AreaRepository areaRepository,
                           CityRepository cityRepository,
                           StateRepository stateRepository,
                           RoomRepository roomRepository,
                           RoomAvailabilityRepository availabilityRepository,
                           EmailProducer emailProducer,
                           S3Service s3Service) {
        this.propertyRepository = propertyRepository;
        this.areaRepository = areaRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.roomRepository = roomRepository;
        this.availabilityRepository = availabilityRepository;
        this.emailProducer = emailProducer;
        this.s3Service = s3Service;
    }

    // ---------------- ADD PROPERTY ----------------
    @Transactional
    @CacheEvict(value = {"property", "propertySearch", "room"}, allEntries = true)
    public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {

        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());

        if(area == null || city == null || state == null)
            throw new InvalidRequestException("Invalid area, city or state");

        Property property = new Property();
        BeanUtils.copyProperties(dto, property);
        property.setArea(area);
        property.setCity(city);
        property.setState(state);

        Property savedProperty = propertyRepository.save(property);

        // Save rooms
        for (RoomsDto roomDto : dto.getRooms()) {
            Rooms room = new Rooms();
            BeanUtils.copyProperties(roomDto, room);
            room.setProperty(savedProperty);
            roomRepository.save(room);
        }

        // Upload images
        dto.setImageUrls(s3Service.uploadFiles(files));

        // Send email notification
        emailProducer.sendEmail(new EmailRequest(
                "ayush.backup1997@gmail.com",
                "Property added",
                "Your property has been successfully added"
        ));

        return dto;
    }

    // ---------------- SEARCH PROPERTY ----------------
    @Cacheable(value = "propertySearch",
            key = "#city + '_' + #date + '_' + #page + '_' + #size + '_' + #sortBy",
            unless = "#result.data == null || #result.data.isEmpty()")
    public APIResponse<Page<PropertyDto>> searchProperty(String city, LocalDate date,
                                                         int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Property> properties = propertyRepository.searchPropertyPaged(city, date, pageable);

        Page<PropertyDto> dtoPage = properties.map(p -> {
            PropertyDto dto = new PropertyDto();
            BeanUtils.copyProperties(p, dto);
            dto.setArea(p.getArea().getName());
            dto.setCity(p.getCity().getName());
            dto.setState(p.getState().getName());
            return dto;
        });

        return new APIResponse<>(200, "Search result", dtoPage);
    }

    // ---------------- FIND PROPERTY ----------------
    @Cacheable(value = "property", key = "#id", unless = "#result.data == null")
    public APIResponse<PropertyDto> findPropertyById(long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));

        PropertyDto dto = new PropertyDto();
        BeanUtils.copyProperties(property, dto);
        dto.setArea(property.getArea().getName());
        dto.setCity(property.getCity().getName());
        dto.setState(property.getState().getName());

        List<RoomsDto> rooms = new ArrayList<>();
        for (Rooms room : property.getRooms()) {
            RoomsDto rd = new RoomsDto();
            BeanUtils.copyProperties(room, rd);
            rooms.add(rd);
        }
        dto.setRooms(rooms);

        return new APIResponse<>(200, "Property found", dto);
    }

    // ---------------- UPDATE PROPERTY ----------------
    @Transactional
    @CacheEvict(value = {"property", "propertySearch"}, allEntries = true)
    public PropertyDto updateProperty(long id, PropertyDto dto) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));

        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());

        propertyRepository.save(property);

        return dto;
    }

    // ---------------- DELETE PROPERTY ----------------
    @Transactional
    @CacheEvict(value = {"property", "propertySearch", "room"}, allEntries = true)
    public void deleteProperty(long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));

        propertyRepository.delete(property);
    }

    // ---------------- ADD/UPDATE ROOM ----------------
    @Transactional
    @CacheEvict(value = {"room"}, allEntries = true)
    public RoomsDto addRoom(long propertyId, RoomsDto dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        Rooms room = new Rooms();
        BeanUtils.copyProperties(dto, room);
        room.setProperty(property);
        roomRepository.save(room);

        return dto;
    }

    @Transactional
    @CacheEvict(value = {"room"}, allEntries = true)
    public RoomsDto updateRoom(long roomId, RoomsDto dto) {
        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        room.setRoomType(dto.getRoomType());
        room.setBasePrice(dto.getBasePrice());

        roomRepository.save(room);
        return dto;
    }

    // ---------------- MANAGE AVAILABILITY ----------------
    @Transactional
    @CacheEvict(value = {"availability"}, allEntries = true)
    public void addOrUpdateAvailability(long roomId, LocalDate date, int count) {
        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        RoomAvailability availability = availabilityRepository
                .findByRoomIdAndAvailableDate(roomId, date)
                .orElse(new RoomAvailability());

        availability.setRoom(room);
        availability.setAvailableDate(date);
        availability.setAvailableCount(count);

        availabilityRepository.save(availability);
    }

    // ---------------- GET ROOM ----------------
    @Cacheable(value = "room", key = "#id")
    public RoomsDto getRoomById(long id) {
        Rooms room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));

        RoomsDto dto = new RoomsDto();
        BeanUtils.copyProperties(room, dto);
        return dto;
    }

    // ---------------- GET ROOM AVAILABILITY ----------------
    @Cacheable(value = "availability", key = "#roomId")
    public List<RoomAvailabilityDto> getTotalRoomsAvailable(long roomId) {
        List<RoomAvailability> list = availabilityRepository.findByRoomId(roomId);
        List<RoomAvailabilityDto> dtoList = new ArrayList<>();

        for (RoomAvailability ra : list) {
            RoomAvailabilityDto dto = new RoomAvailabilityDto();
            dto.setDate(ra.getAvailableDate());
            dto.setAvailableRooms(ra.getAvailableCount());
            dtoList.add(dto);
        }

        return dtoList;
    }
}
