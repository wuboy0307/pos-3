package CartDAO;
use DB;
our @ISA = qw(DB);    # inherits from DB

############################################
#
#   Filename:  DAO.pm (package DAO)
#   Author: 
#
############################################

use strict;
use DBI;
use Log::Log4perl;
use Sys::Hostname;
use Data::Dumper;
use String::Util 'trim';
use Constants;

my $logger = Log::Log4perl->get_logger('opuma');

sub add {
    my $self = shift;
    my $cart = shift;
    my $deviceID = shift;
    
    my $ret ={};
    
    my $id = $cart->{'cart_id'};
    my $total = $cart->{'total'};
    my $paymentCard = $cart->{'payment_card'};
    my $paymentPin = $cart->{'payment_pin'};
    my $items = $cart->{'items'};
    my $pin = $cart->{'payment_pin'};
    my $taxAmount = $cart->{'tax_amount'};
    my $customerEmail = $cart->{'customer_email'};
    my $userID = $cart->{'user_id'};
    my $subTotal = $cart->{'subtotal'};
    my $taxRate = $cart->{'tax_rate'};
    
    $logger->debug("STUFF: $id $total $paymentCard $pin $taxAmount $customerEmail $userID $subTotal $taxRate");
    
    
    my $sql = "insert into cart (device_id,cart_id,user_id,customer_id,subtotal,tax_rate,tax_amount,total,payment_card,payment_pin) " .
              "values (?,?,?,?,?,?,?,?,?,?)";
    
    my $dbh = $self->{'dbh'};
    my $sth = $dbh->prepare($sql);
    $sth->execute($deviceID,$id,$userID,$customerEmail,$subTotal,
                  $taxRate,$taxAmount,$total,$paymentCard,$paymentPin);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        $sth->finish();
        $dbh->disconnect();
        return $ret;
    }
    
    $sth->finish();

    $ret->{Constants::RET_RETURN_MESSAGE} = "Success";
    $ret->{Constants::RET_RETURN_CODE} = Constants::SUCCESS;
    
    return $ret;
}