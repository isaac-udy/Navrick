package com.isaacudy.navrick

import android.support.v4.app.Fragment
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.*

private typealias transactionBlock = (enter: Fragment, exit: Fragment?)-> Unit
private val emptyTransactionBlock: transactionBlock =  {_, _ ->}

class Navrick internal constructor(
        private val bindings: Map<KClass<*>, KClass<out NavrickFragment<*>>>,
        private val activity: NavrickActivity,
        internal val layout: Int,
        private val home: Any?
) {
    internal var backstack: ArrayList<Any> = ArrayList()
    var onFragmentAdded: (fragment: Fragment) -> Unit = {}

    val backstackSize get() = backstack.size
    val currentFragment get() = activity.supportFragmentManager.findFragmentById(layout)

    fun <T : Any> forward(screen: T, transactionBlock: transactionBlock = emptyTransactionBlock){
        backstack.add(screen)
        open(screen, transactionBlock)
    }

    fun back(transactionBlock: transactionBlock = emptyTransactionBlock): Boolean {
        if(backstack.size > 1) {
            backstack.removeAt(backstack.size - 1)
            open(backstack.last(), transactionBlock)
            return true
        }
        return false
    }

    fun <T: Any> backTo(screenClass : KClass<T>, transactionBlock: transactionBlock = emptyTransactionBlock):Boolean {
        while(backstack.size > 1){
            backstack.removeAt(backstack.size - 1)
            if(backstack.last()::class == screenClass){
                open(backstack.last(), transactionBlock)
                return true
            }
        }
        return false
    }

    fun <T: Any> backTo(screen: T, transactionBlock: transactionBlock = emptyTransactionBlock):Boolean {
        while(backstack.size > 1){
            backstack.removeAt(backstack.size - 1)
            if(backstack.last() == screen){
                open(backstack.last(), transactionBlock)
                return true
            }
        }
        return false
    }

    fun <T : Any> replace(screen: T, transactionBlock: transactionBlock = emptyTransactionBlock){
        if(backstack.size > 0) {
            backstack.removeAt(backstack.size - 1)
        }
        open(screen, transactionBlock)
    }

    fun home(transactionBlock: transactionBlock = emptyTransactionBlock){
        backstack.clear()
        clear()
        initialise()
    }

    private fun <T : Any> open(screen: T, transactionBlock: transactionBlock) {
        val view = bindings[screen::class] ?: TODO("NO BINDING")
        val viewInstance = view.createInstance() as NavrickFragment<T>
        viewInstance.bind(screen)

        val existingView = activity.supportFragmentManager.findFragmentById(layout)
        activity.fragmentTransaction {
            transactionBlock(viewInstance, existingView)
            if(existingView != null) {
                remove(existingView)
                (existingView as? NavrickFragment<*>)?.unbind()
            }
            add(layout, viewInstance)
        }

        onFragmentAdded(viewInstance)
    }

    internal fun initialise(){
        val existingFragment = activity.supportFragmentManager.findFragmentById(layout)
        if(existingFragment != null) return
        if(home != null && backstack.size == 0){
            backstack.add(home)
            open(home, emptyTransactionBlock)
        }
    }

    private fun clear(){
        currentFragment?.let {
            activity.fragmentTransaction {
                remove(it)
                (it as? NavrickFragment<*>)?.unbind()
            }
        }
    }
}

class NavrickBuilder {
    private val bindings = HashMap<KClass<*>, KClass<out NavrickFragment<*>>>()
    private var home: Any? = null

    fun addBinding(binding: KClass<*>, view: KClass<out NavrickFragment<*>>): NavrickBuilder {
        if (view.isAbstract) TODO("CAN'T BIND ABSTRACT CLASSES")
        if(!verifyBindingsMatch(binding, view)){
            TODO("CAN'T BIND?")
        }
        bindings.put(binding, view)
        return this
    }

    fun setHome(screen: Any): NavrickBuilder {
        home = screen
        return this
    }

    internal fun verifyBindingsMatch(binding: KClass<*>, view: KClass<out NavrickFragment<*>>): Boolean {
        val navikationClassifier = NavrickFragment::class.starProjectedType.classifier
        val bindingType = binding.createType()

        view.allSupertypes
                .filter { it.classifier == navikationClassifier }
                .forEach {
                    val type = it.arguments[0].type ?: return@forEach
                    if(type == bindingType) return true

                    val typeParameter = type.classifier as? KTypeParameter ?: return@forEach
                    typeParameter.upperBounds.forEach {
                        if(bindingType.isSubtypeOf(it)) return true
                    }
                }
        return false
    }

    fun build(activity: NavrickActivity, layoutId: Int): Navrick {
        return Navrick(bindings.toMap(), activity, layoutId, home)
    }
}
