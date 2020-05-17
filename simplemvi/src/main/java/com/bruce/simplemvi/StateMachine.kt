package com.bruce.simplemvi

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class StateMachine<State, Effect>(
    initialState: State,
    effectHandler: (e: Effect) -> Observable<out Event>,
    private val reducer: (state: State, e: Event) -> Pair<State, Effect?>,
    private val logger: ILogger = Logger()
) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val events: PublishRelay<Event> = PublishRelay.create()
    private val effects = PublishRelay.create<Effect>()

    private fun newEffect(e: Effect) = effects.accept(e)
    private val effectsHandler: Observable<Event> = effects
        .doOnNext { logger.debug("new effect $it") }
        .switchMap(effectHandler)
    private val states = Observable.merge(events, effectsHandler).distinctUntilChanged()
        .doOnNext { logger.debug("new event $it") }
        .scan(initialState, ::reduce)
        .doOnNext { logger.debug("new state $it") }
        .distinctUntilChanged()
        .replay(1)

    fun stateObs(): Observable<State> = states

    fun onUiEvent(event: Event) {
        events.accept(event)
    }

    private fun reduce(state: State, event: Event): State {
        val (newState, effect) = reducer(state, event)
        effect?.let { newEffect(it) }
        return newState
    }

    fun start() {
        compositeDisposable += states.connect()
    }

    fun stop() = compositeDisposable.clear()
}