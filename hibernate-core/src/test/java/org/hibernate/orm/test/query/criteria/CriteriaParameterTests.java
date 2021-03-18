/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.query.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.spi.QueryImplementor;

import org.hibernate.testing.orm.domain.gambit.BasicEntity;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

@DomainModel( annotatedClasses = BasicEntity.class )
@SessionFactory
public class CriteriaParameterTests {
	@Test
	public void testParameterBaseline(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final ParameterExpression<String> parameter = criteriaBuilder.parameter( String.class );
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );
					criteria.where( criteriaBuilder.equal( root.get( "data" ), parameter ) );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );
					query.setParameter( parameter, "fe" );
					query.list();
				}
		);
	}

	@Test
	public void testNamedParameterBaseline(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final ParameterExpression<Collection> parameter = criteriaBuilder.parameter( Collection.class, "datas" );
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );
					Path<?> property = root.get( "data" );
					criteria.where( property.in( parameter ) );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );

					List<String> parameterValue = new ArrayList<>();
					parameterValue.add( "fe" );

					query.setParameter( "datas", parameterValue );
					query.list();
				}
		);
	}

	@Test
	public void testMultiValuedParameterBaseline(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final ParameterExpression<String> parameter = criteriaBuilder.parameter( String.class );
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );

					final CriteriaBuilder.In<Object> inPredicate = criteriaBuilder.in( root.get( "data" ) );
					inPredicate.value( "fe" );
					inPredicate.value( "fi" );
					inPredicate.value( "fo" );
					inPredicate.value( "fum" );

					criteria.where( inPredicate );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );

					query.list();
				}
		);
	}

	@Test
	public void testMultiValuedParameterBaseline2(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );

					final CriteriaBuilder.In<Object> inPredicate = criteriaBuilder.in( root.get( "data" ) );
					final ParameterExpression<String> p1 = criteriaBuilder.parameter( String.class );
					inPredicate.value( p1 );
					final ParameterExpression<String> p2 = criteriaBuilder.parameter( String.class );
					inPredicate.value( p2 );

					criteria.where( inPredicate );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );
					query.setParameter( p1, "fe" );
					query.setParameter( p2, "fi" );
					query.list();
				}
		);
	}

	@Test
	public void testMultiValuedParameterBaseline3(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final ParameterExpression<String> parameter = criteriaBuilder.parameter( String.class );
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );

					final CriteriaBuilder.In<Object> inPredicate = criteriaBuilder.in( root.get( "data" ), parameter );

					criteria.where( inPredicate );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );
					query.setParameter( parameter, "fe" );
					query.list();
				}
		);
	}

	@Test
	public void testMultiValuedParameterBaseline3HQL(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final QueryImplementor query = session.createQuery( "from BasicEntity where data in (:p)" );
					query.setParameter( "p", "fe" );
					query.list();
				}
		);
	}

	@Test
	public void testMultiValuedParameter(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
					final ParameterExpression<Collection> parameter = criteriaBuilder.parameter( Collection.class );
					final CriteriaQuery<BasicEntity> criteria = criteriaBuilder.createQuery( BasicEntity.class );
					final Root<BasicEntity> root = criteria.from( BasicEntity.class );

					final CriteriaBuilder.In<Object> inPredicate = criteriaBuilder.in( root.get( "data" ) );
					inPredicate.value( parameter );

					criteria.where( inPredicate );

					final QueryImplementor<BasicEntity> query = session.createQuery( criteria );
					query.setParameter( parameter, Arrays.asList( "fe", "fi", "fo", "fum" ) );
					query.list();
				}
		);
	}
}
