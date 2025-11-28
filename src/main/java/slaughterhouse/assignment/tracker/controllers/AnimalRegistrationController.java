//package slaughterhouse.assignment.tracker.controllers;
//
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.*;
//import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
//import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
//import slaughterhouse.assignment.tracker.services.ReceptionService;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/animals")
//public class AnimalRegistrationController {
//
//  private final ReceptionService receptionService;
//
//  @Autowired
//  public AnimalRegistrationController(ReceptionService receptionService) {
//    this.receptionService = receptionService;
//  }
//
//  @PostMapping
//  public ResponseEntity<AnimalInfoResponseDTO> registerAnimal(
//      @Valid @RequestBody AnimalRegistrationRequestDTO request) {
//
//    AnimalInfoResponseDTO registeredAnimal = receptionService.registerAnimal(request);
//    return new ResponseEntity<>(registeredAnimal, HttpStatus.CREATED);
//  }
//
//  /**
//   * Get a single animal by its registration number.
//   * Example: GET /api/animals/ABC123
//   */
//  @GetMapping("/{regNo}")
//  public ResponseEntity<AnimalInfoResponseDTO> getAnimalByRegNo(@PathVariable String regNo) {
//    AnimalInfoResponseDTO animal = receptionService.findAnimalByRegNo(regNo);
//
//    if (animal != null) {
//      return ResponseEntity.ok(animal);
//    } else {
//      return ResponseEntity.notFound().build();
//    }
//  }
//
//  /**
//   * Gets a list of animals filtered by date OR origin.
//   * Supported:
//   * - GET /api/animals?date=2025-11-06
//   * - GET /api/animals?origin=Test Farm
//   *
//   * If both parameters are missing, returns 400 Bad Request.
//   */
//  @GetMapping
//  public ResponseEntity<List<AnimalInfoResponseDTO>> getAnimals(
//      @RequestParam(required = false)
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      LocalDate date,
//      @RequestParam(required = false) String origin) {
//
//    if (date == null && origin == null) {
//      // Assignment only requires date/origin filters, not "get all animals"
//      return ResponseEntity.badRequest().build();
//    }
//
//    List<AnimalInfoResponseDTO> animals;
//
//    if (date != null) {
//      animals = receptionService.findAnimalsByDate(date);
//    } else {
//      animals = receptionService.findAnimalsByOrigin(origin);
//    }
//
//    return ResponseEntity.ok(animals);
//  }
//
//  // ------------------- Exception handlers -------------------
//
//  @ResponseStatus(HttpStatus.BAD_REQUEST)
//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public Map<String, List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//    Map<String, List<String>> errors = new HashMap<>();
//
//    List<String> errorMessages = ex.getBindingResult()
//        .getFieldErrors()
//        .stream()
//        .map(error -> error.getField() + ": " + error.getDefaultMessage())
//        .collect(Collectors.toList());
//
//    errors.put("errors", errorMessages);
//    return errors;
//  }
//
//  @ResponseStatus(HttpStatus.CONFLICT)
//  @ExceptionHandler(IllegalArgumentException.class)
//  public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
//    Map<String, String> error = new HashMap<>();
//    error.put("error", ex.getMessage());
//    return error;
//  }
//}
