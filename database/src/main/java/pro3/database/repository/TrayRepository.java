package pro3.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro3.database.entities.PartType;
import pro3.database.entities.Tray;

import java.util.List;
import java.util.Optional;

public interface TrayRepository extends JpaRepository<Tray, Integer>
{
  public Optional<Tray> findById(int id); //Optional handles errors better
  List<Tray> findByType(PartType type);
  List<Tray> findByIsPackaged(boolean isPackaged);
  List<Tray> findByIsFull(boolean isFull);
  List<Tray> findByTypeAndIsFull(PartType type, boolean isFull);
  List<Tray> findByTypeAndIsFullIsFalse(PartType type);//spring understands IsFullIsFalse as a boolean
  List<Tray> findByTypeAndIsFullIsFalseAndIsPackagedIsFalse(PartType type);
}
