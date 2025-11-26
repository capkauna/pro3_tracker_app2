package pro3.database.entities;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Product
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // JPA requirement: protected no-argument constructor
  protected Product()
  {
  }

  // Getter for ID is necessary for the repository to work
  public int getId()
  {
    return id;
  }
}
