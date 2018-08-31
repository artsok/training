package io.github.artsok.tasks.model.dto;

/**
 * DTO is an abbreviation for Data Transfer Object, so it is used to transfer the data between
 * classes and modules of your application. DTO should only contain private fields for your data,
 * getters, setters and constructors. It is not recommended to add business logic methods to such classes,
 * but it is OK to add some util methods.
 *
 *
 *
 * interface PersonDTO {
 *     String getName();
 *     void setName(String name);
 *     //.....
 * }
 *
 * interface PersonDAO {
 *     PersonDTO findById(long id);
 *     void save(PersonDTO person);
 *     //.....
 * }
 */
public interface ClientDTO {
}
