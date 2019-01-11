package ru.rian.dynamics.ui.helpers

interface SnackContainerProvider {

    fun showError(e: Throwable, methodToInvoke: ActionToInvoke)

    class ActionToInvoke {

        private var param: String? = null
        private var actionEmptyParam: (() -> Unit)? = null
        private var actionWithString: ((String?) -> Unit)? = null

        fun invokeMethod() {
            if (actionEmptyParam != null) {
                actionEmptyParam!!.invoke()
            } else {
                actionWithString?.invoke(param)
            }
        }

        constructor (actionEmptyParam: () -> (Unit)) {
            this.actionEmptyParam = actionEmptyParam
        }

        constructor (actionWithString: (String?) -> (Unit), param: String?) {
            this.actionWithString = actionWithString
            this.param = param
        }
    }
}

