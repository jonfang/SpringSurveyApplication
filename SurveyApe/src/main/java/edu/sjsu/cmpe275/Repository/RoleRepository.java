package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Domain.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("roleRepository")
@Primary
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRole(String role);
}