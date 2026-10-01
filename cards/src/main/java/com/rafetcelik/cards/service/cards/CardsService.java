package com.rafetcelik.cards.service.cards;

import com.rafetcelik.cards.constants.CardsConstants;
import com.rafetcelik.cards.dto.CardsDto;
import com.rafetcelik.cards.entity.Cards;
import com.rafetcelik.cards.excepiton.CardAlreadyExistsException;
import com.rafetcelik.cards.excepiton.ResourceNotFoundException;
import com.rafetcelik.cards.mapper.CardsMapper;
import com.rafetcelik.cards.repository.CardsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardsService implements ICardsService{
    private final CardsRepository cardsRepository;

    @Override
    public void createCard(CardsDto cardsDto) {
        Optional<Cards> optionalCards = cardsRepository.findByMobileNumber(cardsDto.getMobileNumber());
        if (optionalCards.isPresent()) {
            throw new CardAlreadyExistsException("Card already exists for the given mobile number: " + cardsDto.getMobileNumber());
        }
        cardsRepository.save(createNewCard(cardsDto.getMobileNumber()));
    }

    private Cards createNewCard(String mobileNumber) {
        Cards newCard = new Cards();
        long randomCardNumber = 1000000000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return newCard;
    }

    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards card = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card not found for the given mobile number: " + mobileNumber)
        );
        return CardsMapper.mapToCardsDto(card, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards card = cardsRepository.findByCardNumber(cardsDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card not found for the given card number: " + cardsDto.getCardNumber())
        );
        CardsMapper.mapToCards(cardsDto, card);
        cardsRepository.save(card);
        return true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards card = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card not found for the given mobile number: " + mobileNumber)
        );
        cardsRepository.deleteById(card.getCardId());
        return true;
    }
}
