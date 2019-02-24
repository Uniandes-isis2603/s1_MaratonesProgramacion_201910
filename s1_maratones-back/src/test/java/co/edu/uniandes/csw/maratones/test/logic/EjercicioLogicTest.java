/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.maratones.test.logic;

import co.edu.uniandes.csw.maratones.ejb.EjercicioLogic;
import co.edu.uniandes.csw.maratones.entities.EjercicioEntity;
import co.edu.uniandes.csw.maratones.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.maratones.persistence.EjercicioPersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author Angel Rodriguez aa.rodriguezv
 */
@RunWith(Arquillian.class)
public class EjercicioLogicTest {
    
          
  private PodamFactory factory = new PodamFactoryImpl();
    
  
  @PersistenceContext
   private EntityManager em;

  /**
     * Variable para marcar las transacciones del em anterior cuando se
     * crean/borran datos para las pruebas.
     */
    @Inject
    private UserTransaction utx;

    
     /**
     * Lista que tiene los datos de prueba.
     */
    private List<EjercicioEntity> data = new ArrayList<EjercicioEntity>();

  
   
    
    @Inject 
    private EjercicioLogic ejercicioLogic;
    
    
    
    
     /**
     *
     * @return Devuelve el jar que Arquillian va a desplegar en el Glassfish
     * embebido. El jar contiene las clases de Editorial, el descriptor de la
     * base de datos y el archivo beans.xml para resolver la inyección de
     * dependencias.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EjercicioEntity.class.getPackage())
                .addPackage(EjercicioLogic.class.getPackage())
                .addPackage(EjercicioPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }
    
    
    /**
     * Configuración inicial de la prueba.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
      /**
     * Limpia las tablas que están implicadas en la prueba.
     */
    private void clearData() {
        em.createQuery("delete from EjercicioEntity").executeUpdate();
    }
    
    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            EjercicioEntity entity = factory.manufacturePojo(EjercicioEntity.class);

            em.persist(entity);
            data.add(entity);

        }
    }

    
    
    @Test 
    public void createLenguajeTest() throws BusinessLogicException
    {
        EjercicioEntity newEntity = factory.manufacturePojo(EjercicioEntity.class);
        EjercicioEntity result = ejercicioLogic.createEjercicio(newEntity);
        Assert.assertNotNull(result);
        EjercicioEntity entity = em.find(EjercicioEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getNombre(), entity.getNombre());
        
    }
    
    
    
    @Test(expected = BusinessLogicException.class)
    
    public void createLenguajeConMismoNombreTest() throws BusinessLogicException {
        EjercicioEntity newEntity = factory.manufacturePojo(EjercicioEntity.class);
        newEntity.setNombre(data.get(0).getNombre());
        ejercicioLogic.createEjercicio(newEntity);
    }
}
