package pro3.service_tier.controllers;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pro3.service_tier.services.ReceptionService;
import pro3.shared_dtos.dtos.Animal.AnimalInfoResponseDTO;
import pro3.shared_dtos.dtos.Animal.AnimalRegistrationRequestDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animals")
public class AnimalRegistrationController {

    private final ReceptionService receptionService;

    public AnimalRegistrationController(ReceptionService receptionService) {
        this.receptionService = receptionService;
    }

  /**
   * Register a new animal.
   * POST /api/animals
   */
  @PostMapping
  //changed to void for rabbitmq
  public ResponseEntity<Void> registerAnimal(@Valid @RequestBody AnimalRegistrationRequestDTO request)
  {
    //DTO registeredAnimal =
    receptionService.registerAnimal(request);
    //return new ResponseEntity<>(registeredAnimal, HttpStatus.CREATED);
    //since we're not sure whether the request will be handled immediately or not, created is not appropriate, so accepted is better
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

    /**
     * Get a single animal by its registration number.
     * Example: GET /api/animals/ABC123
     */
    @GetMapping("/{regNo}")
    public ResponseEntity<AnimalInfoResponseDTO> getAnimalByRegNo(
            @PathVariable String regNo) {

        AnimalInfoResponseDTO animal = receptionService.findAnimalByRegNo(regNo);

        if (animal != null) {
            return ResponseEntity.ok(animal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/animals/by-date?date=YYYY-MM-DD
     */
    @GetMapping("/by-date")
    public ResponseEntity<List<AnimalInfoResponseDTO>> getAnimalsByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AnimalInfoResponseDTO> animals = receptionService.findAnimalsByDate(date);
        return ResponseEntity.ok(animals);
    }

    /**
     * GET /api/animals/by-origin?origin=SomeFarm
     */
    @GetMapping("/by-origin")
    public ResponseEntity<List<AnimalInfoResponseDTO>> getAnimalsByOrigin(
            @RequestParam String origin) {

        List<AnimalInfoResponseDTO> animals = receptionService.findAnimalsByOrigin(origin);
        return ResponseEntity.ok(animals);
    }

    // ----------------- Error handlers -----------------

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        errors.put("errors", errorMessages);
        return errors;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}
