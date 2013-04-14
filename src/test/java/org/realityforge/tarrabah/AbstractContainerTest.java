package org.realityforge.tarrabah;

import javax.annotation.Nullable;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.jboss.weld.manager.BeanManagerImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractContainerTest
{
  @Nullable
  private CdiContainer _cdiContainer;
  @Nullable
  private CreationalContext _context;

  @SuppressWarnings( "unchecked" )
  @BeforeMethod
  public void setupContainer()
    throws Exception
  {
    _cdiContainer = CdiContainerLoader.getCdiContainer();
    _cdiContainer.boot();
    _cdiContainer.getContextControl().startContexts();
    _context = _cdiContainer.getBeanManager().createCreationalContext( null );
    final InjectionTarget injectionTarget =
      _cdiContainer.getBeanManager()
        .createInjectionTarget( _cdiContainer.getBeanManager().createAnnotatedType( getClass() ) );
    injectionTarget.inject( this, _context );
    injectionTarget.postConstruct( this );
  }

  @AfterMethod
  public void shutdownContainer()
    throws Exception
  {
    if ( null != _context )
    {
      _context.release();
      _context = null;
    }
    if ( null != _cdiContainer )
    {
      _cdiContainer.shutdown();
      _cdiContainer = null;
    }
  }
}
