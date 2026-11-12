package com.suporte.iop.reposity;

import com.suporte.iop.entity.IOPEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class IOPRepository implements PanacheRepository<IOPEntity>
{


}
