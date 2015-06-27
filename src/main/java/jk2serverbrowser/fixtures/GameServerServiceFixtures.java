/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jk2serverbrowser.fixtures;

import jk2serverbrowser.IGameServerService;
import jk2serverbrowser.Tuple;
import rx.Observable;

/**
 *
 * @author markus
 */
public class GameServerServiceFixtures implements IGameServerService {

    @Override
    public Observable<Tuple<String[], Long>> getServerInfo(Tuple<String, Integer> ip) {
        throw new UnsupportedOperationException("Get server info fixtures not implemented."); 
    }

    @Override
    public Observable<Tuple<String[], Long>> getServerStatus(Tuple<String, Integer> ip) {
        return Observable.just(new Tuple(new String[] {"getServerStatusResponse", "\\version\\JK2MP: v1.04mv linux-amd64 May 29 2015\\sv_maxclients\\32\\mv_httpdownloads\\1\\g_blueteam\\Rebellion\\g_redteam\\Empire\\g_jediVmerc\\0\\g_maxGameClients\\0\\capturelimit\\20\\g_weapondisable\\0\\g_forcePowerDisable\\0\\sv_allowDownload\\1\\sv_floodProtect\\0\\sv_maxPing\\0\\sv_minPing\\0\\sv_maxRate\\100000\\sv_hostname\\jk2mv.org  ^1CTF PUG\\g_gametype\\7\\g_duelWeaponDisable\\0\\g_forceBasedTeams\\0\\duel_fraglimit\\10\\g_maxForceRank\\7\\g_saberLocking\\1\\g_privateDuel\\1\\timelimit\\20\\fraglimit\\0\\dmflags\\0\\g_forceRegenTime\\200\\protocol\\16\\JK2MV\\1.0rc2\\g_maxHolocronCarry\\3\\g_needpass\\1\\mapname\\ctf_talay\\sv_privateClients\\5\\sv_allowAnonymous\\0\\bot_minplayers\\0\\gamename\\basejk", "143 4 \"Mock^1Player\"", ""}, 123L));
    }
    
}
