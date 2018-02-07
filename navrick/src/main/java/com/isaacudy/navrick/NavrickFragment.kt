package com.isaacudy.navrick

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.collections.HashMap

private const val NAVRICK_FRAGMENT_ID = "com.isaacudy.navrick.NavrickFragment.id"

private const val ERROR_UNBOUND_VIEW = "This NavrickFragment was created without binding to an instance of it's generic type T"
private const val ERROR_INCORRECTLY_BOUND_VIEW = "This NavrickFragment was bound to an instance of that did not match it's generic type T"

abstract class NavrickFragment<in T : Any> : Fragment() {

    private lateinit var _id: String
    abstract val layout: Int

    lateinit var navrick: Navrick

    internal fun bind(t: T) {
        _id = UUID.randomUUID().toString()

        val bundle = Bundle()
        bundle.putString(NAVRICK_FRAGMENT_ID, _id)
        arguments = bundle

        NavrickFragmentBindingStorage.storage.put(_id, t)
    }

    internal fun unbind() {
        NavrickFragmentBindingStorage.storage.remove(_id)
    }

    abstract fun onViewBound(t: T)

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _id = arguments.getString(NAVRICK_FRAGMENT_ID)

        if (activity is NavrickActivity) {
            navrick = (activity as NavrickActivity).navrick
        }
        else {
            TODO("NICE EXCEPTION")
        }
    }

    final override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(layout, container, false) ?: TODO("INFLATER IS NULL?")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = NavrickFragmentBindingStorage.storage[_id] ?: throw RuntimeException(ERROR_UNBOUND_VIEW)

        try {
            @Suppress("UNCHECKED_CAST")
            val unchecked = binding as T
        }
        catch (ex: Exception) {
            throw RuntimeException(ERROR_UNBOUND_VIEW)
        }

        onViewBound(binding)
    }

    final override fun setArguments(args: Bundle?) {
        val arguments = args ?: Bundle()
        arguments.apply {
            if (getString(NAVRICK_FRAGMENT_ID) == null) {
                putString(NAVRICK_FRAGMENT_ID, _id)
            }
        }
        super.setArguments(arguments)
    }

    open fun onBackPressed(): Boolean {
        return false
    }
}

internal object NavrickFragmentBindingStorage {
    val storage: HashMap<String, Any> = HashMap()
}