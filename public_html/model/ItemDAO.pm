package ItemDAO;
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

sub now() {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};
    
    my ($ret, $rc, $rm);
    
    my $sql = "select current_timestamp as now";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }
    
    if (my $ref = $sth->fetchrow_hashref()) {
        $ret = $ref->{'now'};
    }
    $sth->finish();
        
    $dbh->disconnect();
    
    return $ret;
}

sub sync() {
    my $self = shift;
    my $deviceID = shift;

    my $now = $self->now();
    
    $deviceID =~ s/'/''/g;
    my $sql = "select * from item i " . 
              "where i.updateTimestamp > " .
              "(select lastTimestamp from watermark w where device_id = '$deviceID') " .
              "and i.updateTimestamp < '$now'";
    
    my $ret = $self->getAll($sql);
    
    $sql = "update watermark set lastTimestamp = timestamp('$now') where device_id = '$deviceID'";
    my $dbh = $self->{'dbh'};
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }
    
    $sth->finish();
    $dbh->disconnect();
    
    return $ret;

}

sub update() {
    my $self = shift;
    my $itemID = shift;
    my $description = shift;
    my $price = shift;
    my $deviceID = shift;
    my $userID = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "insert into item (item_id,description,price,update_device_id,update_user_id) " .
              "values (?, ?, ?, ?, ?) " . 
              "on duplicate key update description=?,price=?,update_device_id=?,update_user_id=?";
    
    $logger->debug("SQL $sql");
    my ($ret);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute($itemID,$description,$price,$deviceID,
                  $userID,$description,$price,$deviceID,$userID);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        $dbh->disconnect();
        return $ret;
    }
    
    $sth->finish();
    
    $ret->{Constants::RET_RETURN_MESSAGE} = "Success";
    $ret->{Constants::RET_RETURN_CODE} = 0;
    
    $dbh->disconnect();
    
    return $ret;
    
}

sub add {
    my $self = shift;
    my $itemID = shift;
    my $description = shift;
    my $price = shift;
    my $deviceID = shift;
    my $userID = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select count(*) as count from item where item_id = ?";
    
    my ($ret, $rc, $rm);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute($itemID);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        $sth->finish();
        $dbh->disconnect();
        return $ret;
    }
    
    my $count;
    if (my $ref = $sth->fetchrow_hashref()) {
        $count = $ref->{'count'};
    }
    $sth->finish();
    
    if ($count > 0) {
        $rm = "Item already exists: $itemID";
        $rc = 1;
    } else {
        my $sql = "insert into item (item_id,description,price,device_id,create_user_id) " .
                  "values (?,?,?,?,?)";
        
        $sth = $dbh->prepare($sql);
        $sth->execute($itemID,$description,$price,$deviceID,$userID);
        if ($sth->err() > 0) {
            $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
            $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
            $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
            $sth->finish();
            $dbh->disconnect();
            return $ret;
        }
        
        $sth->finish();
        
        $rm = "Success";
        $rc = 0;
    }
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub reset() {
    my $self = shift;
    my $deviceID = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "insert into watermark (device_id, lastTimestamp) " .
              "values (?, timestamp('1999-01-01 00:00:00')) " . 
              "on duplicate key update lastTimestamp=timestamp('1999-01-01 00:00:00')";
    
    $logger->debug("SQL $sql");
    my ($rm, $rc, $ret);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute($deviceID);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        $sth->finish();
        return $ret;
    }
    
    $sth->finish();
    
    $rm = "Success";
    $rc = 0;
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
    
}

sub getItems() {
    my $self = shift;

    my $sql = "select * from item order by description";
    
    my $ret = $self->getAll($sql);
    
    return $ret;
}

sub getAll() {
    my $self = shift;
    my $sql = shift;
    
    my ($ret, $ref, @a, $count);
    
    my $dbh = $self->{'dbh'};
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        $sth->finish();
        return $ret;
    }

    $count = 0;
    while ($ref = $sth->fetchrow_hashref()) {
        push @a, $ref;
        $count++;
    }
    $sth->finish();
    
    $ret->{Constants::RET_RETURN_MESSAGE} = "Success: $count";
    $ret->{Constants::RET_RETURN_CODE} = 0;
    $ret->{'data'} = \@a;
    
    $dbh->disconnect();
    
    return $ret;
}

sub get() {
    my $self = shift;
    my $id = shift;
    
    my $sql = "select * from item where item_id = ?";
    
    my ($ret, $rc, $rm, $ref, @a, $count);
    
    my $dbh = $self->{'dbh'};
    my $sth = $dbh->prepare($sql);
    $sth->execute($id);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }

    $count = 0;
    if ($ref = $sth->fetchrow_hashref()) {
        $ret = $ref;
        $rm = "Success: $count";
        $rc = 0;
    } else {
        $rm = "No item found";
        $rc = 1;
    }
    $sth->finish();
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;

    $dbh->disconnect();
    
    return $ret;
}

1;