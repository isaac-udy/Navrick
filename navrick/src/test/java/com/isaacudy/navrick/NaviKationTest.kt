package com.isaacudy.navrick

import org.junit.Assert.*
import org.junit.Test


class NaviKationTest {

    @Test
    fun verifyBindingsMatch_bindingsMatch_success(){
        assertTrue(NavrickBuilder().verifyBindingsMatch(TestType::class, TestView::class))
    }

    @Test
    fun verifyBindingsMatch_nestedBindingsMatch_success(){
        assertTrue(NavrickBuilder().verifyBindingsMatch(TestType::class, TestNestedView::class))
    }

    @Test
    fun verifyBindingsMatch_genericBindingsMatch_success(){
        assertTrue(NavrickBuilder().verifyBindingsMatch(GenericBase::class, TestGenericView::class))
        assertTrue(NavrickBuilder().verifyBindingsMatch(GenericOne::class, TestGenericView::class))
        assertTrue(NavrickBuilder().verifyBindingsMatch(GenericTwo::class, TestGenericView::class))
    }

    @Test
    fun verifyBindingsMatch_bindingsDoNotMatch_fail(){
        assertFalse(NavrickBuilder().verifyBindingsMatch(String::class, TestView::class))
    }
}

class TestType
class TestView : NavrickFragment<TestType>(){
    override val layout = 0
    override fun onViewBound(t: TestType) { }
}

open class TestNestedSuper : NavrickFragment<TestType>(){
    override val layout = 0
    override fun onViewBound(t: TestType) { }
}
class TestNestedView : TestNestedSuper()

open class GenericBase
open class GenericOne: GenericBase()
class GenericTwo: GenericOne()
class TestGenericView<in T: GenericBase> : NavrickFragment<T>(){
    override val layout = 0
    override fun onViewBound(t: T) {}
}