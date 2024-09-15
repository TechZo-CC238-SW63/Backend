package com.techzo.cambiazo.exchanges.application.internal.commandservices;

import com.techzo.cambiazo.exchanges.domain.model.commands.CreateSubscriptionCommand;
import com.techzo.cambiazo.exchanges.domain.model.commands.UpdateSubscriptionCommand;
import com.techzo.cambiazo.exchanges.domain.model.entities.Plan;
import com.techzo.cambiazo.exchanges.domain.model.entities.Subscription;
import com.techzo.cambiazo.exchanges.domain.model.entities.User;
import com.techzo.cambiazo.exchanges.domain.services.ISubscriptionCommandService;
import com.techzo.cambiazo.exchanges.infrastructure.persistence.jpa.IPlanRepository;
import com.techzo.cambiazo.exchanges.infrastructure.persistence.jpa.ISubscriptionRepository;
import com.techzo.cambiazo.exchanges.infrastructure.persistence.jpa.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionCommandServiceImpl implements ISubscriptionCommandService {

    private final ISubscriptionRepository subscriptionRepository;
    private final IPlanRepository planRepository;

    private final IUserRepository userRepository;

    public SubscriptionCommandServiceImpl(ISubscriptionRepository subscriptionRepository, IPlanRepository planRepository, IUserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<Subscription>handle(CreateSubscriptionCommand command){

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User with the given id does not exist"));
        var result=subscriptionRepository.findByUserId(user);
        if(result.isPresent()){
            throw new IllegalArgumentException("User already has a subscription");
        }else{
            Plan plan = planRepository.findById(command.planId())
                    .orElseThrow(() -> new IllegalArgumentException("Plan with the given id does not exist"));
            var subscription = new Subscription(command, plan, user);
            subscriptionRepository.save(subscription);
            return Optional.of(subscription);
        }
    }

    @Override
    @Transactional
    public Optional<Subscription>handle(UpdateSubscriptionCommand command){
        var result = subscriptionRepository.findById(command.id());
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Subscription does not exist");
        }
        var subscriptionToUpdate = result.get();
        try {
            Subscription subscription = subscriptionRepository.findById(command.id())
                    .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

            Plan plan = planRepository.findById(subscription.getPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

            User user = userRepository.findById(subscription.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusMonths(1);


            var updatedSubscription = subscriptionRepository.save(subscriptionToUpdate.updateInformation(startDate,endDate,command.state(),plan,user));
            return Optional.of(updatedSubscription);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while updating subscription: " + e.getMessage());
        }
    }
}
